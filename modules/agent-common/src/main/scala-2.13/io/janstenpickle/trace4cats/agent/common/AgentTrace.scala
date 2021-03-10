package io.janstenpickle.trace4cats.agent.common

import cats.Applicative
import cats.effect.kernel.{Async, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import fs2.{Chunk, Pipe}
import io.janstenpickle.trace4cats.kernel.{SpanExporter, SpanSampler}
import io.janstenpickle.trace4cats.meta.{PipeTracer, TracedSpanExporter}
import io.janstenpickle.trace4cats.model.{AttributeValue, CompletedSpan, TraceProcess}
import io.janstenpickle.trace4cats.rate.sampling.RateSpanSampler

import java.net.InetAddress

object AgentTrace {
  def apply[F[_]: Async](
    exporterName: String,
    exporterAttributes: Map[String, AttributeValue],
    listenerPort: Int,
    sampleRate: Option[Double],
    bufferSize: Int,
    exporter: SpanExporter[F, Chunk]
  ): F[(Pipe[F, CompletedSpan, CompletedSpan], SpanExporter[F, Chunk])] = for {
    hostname <- Sync[F].blocking(InetAddress.getLocalHost.getHostName)
    process = TraceProcess("trace4cats-agent", Map("hostname" -> hostname))

    sampler <- sampleRate.fold(Applicative[F].pure(SpanSampler.always[F]))(rate =>
      RateSpanSampler.create[F](bufferSize, rate)
    )

    pipe = PipeTracer[F](
      Map("listen.protocol" -> "udp", "listen.format" -> "avro", "listen.port" -> listenerPort),
      process,
      sampler
    )
  } yield pipe -> TracedSpanExporter[F](exporterName, exporterAttributes, process, sampler, exporter)
}
