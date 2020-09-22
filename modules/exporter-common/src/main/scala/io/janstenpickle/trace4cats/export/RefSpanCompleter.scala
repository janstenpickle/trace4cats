package io.janstenpickle.trace4cats.`export`

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.syntax.functor._
import io.janstenpickle.trace4cats.kernel.SpanCompleter
import io.janstenpickle.trace4cats.model.CompletedSpan

import scala.collection.immutable.Queue

/**
  * RefSpanCompleter collects all spans in a queue within an atomic reference
  * Best used for testing purposes
  */
class RefSpanCompleter[F[_]](ref: Ref[F, Queue[CompletedSpan]]) extends SpanCompleter[F] {
  override def complete(span: CompletedSpan): F[Unit] = ref.update(_.enqueue(span))
  def get: F[Queue[CompletedSpan]] = ref.get
}

object RefSpanCompleter {
  def apply[F[_]: Sync]: F[RefSpanCompleter[F]] = Ref.of(Queue.empty[CompletedSpan]).map(new RefSpanCompleter(_))
  def unsafe[F[_]: Sync]: RefSpanCompleter[F] = new RefSpanCompleter(Ref.unsafe(Queue.empty[CompletedSpan]))
}