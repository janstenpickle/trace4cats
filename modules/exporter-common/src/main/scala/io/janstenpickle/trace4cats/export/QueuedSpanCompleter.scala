package io.janstenpickle.trace4cats.`export`

import cats.effect.concurrent.Ref
import cats.effect.syntax.bracket._
import cats.effect.syntax.concurrent._
import cats.effect.{Concurrent, Resource, Timer}
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.monad._
import fs2.{Chunk, Stream}
import fs2.concurrent.Queue
import io.chrisdavenport.log4cats.Logger
import io.janstenpickle.trace4cats.kernel.{SpanCompleter, SpanExporter}
import io.janstenpickle.trace4cats.model.{Batch, CompletedSpan, TraceProcess}

import scala.concurrent.duration._

object QueuedSpanCompleter {
  def apply[F[_]: Concurrent: Timer: Logger](
    process: TraceProcess,
    exporter: SpanExporter[F, Chunk],
    bufferSize: Int,
    batchSize: Int,
    batchTimeout: FiniteDuration,
  ): Resource[F, SpanCompleter[F]] = {
    val realBufferSize = if (bufferSize < batchSize * 5) batchSize * 5 else bufferSize

    def write(inFlight: Ref[F, Int], queue: Queue[F, CompletedSpan], exporter: SpanExporter[F, Chunk]): F[Unit] =
      queue.dequeue
        .groupWithin(batchSize, batchTimeout)
        .map(spans => Batch(spans))
        .evalMap { batch =>
          exporter.exportBatch(batch).guarantee(inFlight.update(_ - batch.spans.size))
        }
        .compile
        .drain
        .onError {
          case th => Logger[F].warn(th)("Failed to export spans")
        }

    for {
      inFlight <- Resource.liftF(Ref.of(0))
      queue <- Resource.liftF(Queue.bounded[F, CompletedSpan](realBufferSize))
      _ <- Resource.make(
        Stream
          .retry(write(inFlight, queue, exporter), 5.seconds, _ + 1.second, Int.MaxValue)
          .compile
          .drain
          .start
      )(fiber => Timer[F].sleep(50.millis).whileM_(inFlight.get.map(_ != 0)) >> fiber.cancel)
    } yield new SpanCompleter[F] {
      override def complete(span: CompletedSpan.Builder): F[Unit] = {
        val enqueue = queue.enqueue1(span.build(process)) >> inFlight.update { current =>
          if (current == realBufferSize) current
          else current + 1
        }

        inFlight.get
          .map(_ == realBufferSize)
          .ifM(Logger[F].warn(s"Failed to enqueue new span, buffer is full of $realBufferSize"), enqueue)
      }
    }
  }
}
