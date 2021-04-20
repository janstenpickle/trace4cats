// Adapted from Natchez
// Copyright (c) 2019 by Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package io.janstenpickle.trace4cats.example

import cats.data.Kleisli
import cats.effect.{Concurrent, ExitCode, IO, IOApp, Resource, Sync}
import cats.instances.int._
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.parallel._
import cats.syntax.partialOrder._
import cats.{Monad, Order, Parallel}
import io.janstenpickle.trace4cats.`export`.CompleterConfig
import io.janstenpickle.trace4cats.avro.AvroSpanCompleter
import io.janstenpickle.trace4cats.inject.{Trace => T4CTrace}
import io.janstenpickle.trace4cats.kernel.SpanSampler
import io.janstenpickle.trace4cats.model.TraceProcess
import io.janstenpickle.trace4cats.natchez.Trace4CatsTracer
import io.janstenpickle.trace4cats.natchez.conversions.fromNatchez._
import natchez.{EntryPoint, Trace, Span => NatchezSpan}

import scala.concurrent.duration._
import scala.util.Random
import cats.effect.Temporal

/** Adapted from https://github.com/tpolecat/natchez/blob/b995b0ebf7b180666810f4edef46dce959596ace/modules/examples/src/main/scala/Example.scala
  *
  * This example demonstrates how to use Natchez to implicitly pass spans around the callstack.
  */
object NatchezExample extends IOApp {
  def entryPoint[F[_]: Concurrent: ContextShift: Temporal](process: TraceProcess
  ): Resource[F, EntryPoint[F]] =
    AvroSpanCompleter.udp[F](blocker, process, config = CompleterConfig(batchTimeout = 50.millis)).map { completer =>
      Trace4CatsTracer.entryPoint[F](SpanSampler.probabilistic[F](0.05), completer)
    }

  // Intentionally slow parallel quicksort, to demonstrate branching. If we run too quickly it seems
  // to break Jaeger with "skipping clock skew adjustment" so let's pause a bit each time.
  def qsort[F[_]: Monad: Parallel: Trace: Temporal, A: Order](as: List[A]): F[List[A]] =
    Trace[F].span(as.mkString(",")) {
      Temporal[F].sleep(10.milli) *> {
        as match {
          case Nil => Monad[F].pure(Nil)
          case h :: t =>
            val (a, b) = t.partition(_ <= h)
            (qsort[F, A](a), qsort[F, A](b)).parMapN(_ ++ List(h) ++ _)
        }
      }
    }

  // Demonstrate implicit conversion from Natchez trace to Trace4Cats
  // use io.janstenpickle.trace4cats.natchez.conversions._ to do this
  def convertedTrace[F[_]: T4CTrace]: F[Unit] = T4CTrace[F].put("attribute", "test")

  def runF[F[_]: Sync: Trace: Parallel: Temporal]: F[Unit] =
    Trace[F].span("Sort some stuff!") {
      for {
        as <- Sync[F].delay(List.fill(100)(Random.nextInt(1000)))
        _ <- qsort[F, Int](as)
        _ <- convertedTrace[F]
      } yield ()
    }

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      blocker <- Resource.unit[IO]
      ep <- entryPoint[IO](blocker, TraceProcess("natchez"))
    } yield ep)
      .use { ep =>
        ep.root("this is the root span").use { span =>
          runF[Kleisli[IO, NatchezSpan[IO], *]].run(span)
        }
      }
      .as(ExitCode.Success)
}
