package io.janstenpickle.trace4cats.strackdriver

import cats.effect.kernel.{Async, Resource}
import fs2.Chunk
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.janstenpickle.trace4cats.`export`.{CompleterConfig, QueuedSpanCompleter}
import io.janstenpickle.trace4cats.kernel.SpanCompleter
import io.janstenpickle.trace4cats.model.TraceProcess
import io.janstenpickle.trace4cats.strackdriver.oauth.TokenProvider
import io.janstenpickle.trace4cats.strackdriver.project.ProjectIdProvider
import org.http4s.client.Client

import scala.concurrent.ExecutionContext

object StackdriverHttpSpanCompleter {
  def serviceAccountBlazeClient[F[_]: Async](
    ec: ExecutionContext, //TODO: keep parameter or replace with EC.global as recommended?
    process: TraceProcess,
    projectId: String,
    serviceAccountPath: String,
    config: CompleterConfig = CompleterConfig(),
  ): Resource[F, SpanCompleter[F]] =
    for {
      implicit0(logger: Logger[F]) <- Resource.eval(Slf4jLogger.create[F])
      exporter <- StackdriverHttpSpanExporter.blazeClient[F, Chunk](ec, projectId, serviceAccountPath)
      completer <- QueuedSpanCompleter[F](process, exporter, config)
    } yield completer

  def blazeClient[F[_]: Async](
    ec: ExecutionContext, //TODO: keep parameter or replace with EC.global as recommended?
    process: TraceProcess,
    serviceAccountName: String = "default",
    config: CompleterConfig = CompleterConfig(),
  ): Resource[F, SpanCompleter[F]] =
    for {
      implicit0(logger: Logger[F]) <- Resource.eval(Slf4jLogger.create[F])
      exporter <- StackdriverHttpSpanExporter.blazeClient[F, Chunk](ec, serviceAccountName)
      completer <- QueuedSpanCompleter[F](process, exporter, config)
    } yield completer

  def serviceAccount[F[_]: Async](
    process: TraceProcess,
    client: Client[F],
    projectId: String,
    serviceAccountPath: String,
    config: CompleterConfig = CompleterConfig(),
  ): Resource[F, SpanCompleter[F]] =
    for {
      implicit0(logger: Logger[F]) <- Resource.eval(Slf4jLogger.create[F])
      exporter <- Resource.eval(StackdriverHttpSpanExporter[F, Chunk](projectId, serviceAccountPath, client))
      completer <- QueuedSpanCompleter[F](process, exporter, config)
    } yield completer

  def apply[F[_]: Async](
    process: TraceProcess,
    client: Client[F],
    serviceAccountName: String = "default",
    config: CompleterConfig = CompleterConfig(),
  ): Resource[F, SpanCompleter[F]] =
    for {
      implicit0(logger: Logger[F]) <- Resource.eval(Slf4jLogger.create[F])
      exporter <- Resource.eval(StackdriverHttpSpanExporter[F, Chunk](client, serviceAccountName))
      completer <- QueuedSpanCompleter[F](process, exporter, config)
    } yield completer

  def fromProviders[F[_]: Async](
    process: TraceProcess,
    projectIdProvider: ProjectIdProvider[F],
    tokenProvider: TokenProvider[F],
    client: Client[F],
    config: CompleterConfig = CompleterConfig(),
  ): Resource[F, SpanCompleter[F]] =
    for {
      implicit0(logger: Logger[F]) <- Resource.eval(Slf4jLogger.create[F])
      exporter <- Resource.eval(StackdriverHttpSpanExporter[F, Chunk](projectIdProvider, tokenProvider, client))
      completer <- QueuedSpanCompleter[F](process, exporter, config)
    } yield completer
}
