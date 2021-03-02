package io.janstenpickle.trace4cats.avro

import cats.effect.kernel.{Async, Resource}
import fs2.Chunk
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.janstenpickle.trace4cats.`export`.{CompleterConfig, QueuedSpanCompleter}
import io.janstenpickle.trace4cats.kernel.SpanCompleter
import io.janstenpickle.trace4cats.model.TraceProcess

object AvroSpanCompleter {
  def udp[F[_]: Async](
    process: TraceProcess,
    host: String = agentHostname,
    port: Int = agentPort,
    config: CompleterConfig = CompleterConfig(),
  ): Resource[F, SpanCompleter[F]] =
    for {
      implicit0(logger: Logger[F]) <- Resource.eval(Slf4jLogger.create[F])
      exporter <- AvroSpanExporter.udp[F, Chunk](host, port)
      completer <- QueuedSpanCompleter[F](process, exporter, config)
    } yield completer

  def tcp[F[_]: Async](
    process: TraceProcess,
    host: String = agentHostname,
    port: Int = agentPort,
    config: CompleterConfig = CompleterConfig(),
  ): Resource[F, SpanCompleter[F]] = {
    for {
      implicit0(logger: Logger[F]) <- Resource.eval(Slf4jLogger.create[F])
      exporter <- AvroSpanExporter.tcp[F, Chunk](host, port)
      completer <- QueuedSpanCompleter[F](process, exporter, config)
    } yield completer
  }
}
