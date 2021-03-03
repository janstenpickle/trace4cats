package io.janstenpickle.trace4cats.opentelemetry.otlp

import cats.Foldable
import cats.effect.kernel.{Async, Resource, Temporal}
import io.janstenpickle.trace4cats.`export`.HttpSpanExporter
import io.janstenpickle.trace4cats.kernel.SpanExporter
import io.janstenpickle.trace4cats.model.Batch
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext

object OpenTelemetryOtlpHttpSpanExporter {
  def blazeClient[F[_]: Async, G[_]: Foldable](
    ec: ExecutionContext, //TODO: keep param or use Async.ec or EC.global?
    host: String = "localhost",
    port: Int = 55681
  ): Resource[F, SpanExporter[F, G]] =
    BlazeClientBuilder[F](ec).resource.evalMap(apply[F, G](_, host, port))

  def apply[F[_]: Temporal, G[_]: Foldable](
    client: Client[F],
    host: String = "localhost",
    port: Int = 55681
  ): F[SpanExporter[F, G]] =
    HttpSpanExporter[F, G, String](
      client,
      s"http://$host:$port/v1/trace",
      (batch: Batch[G]) => Convert.toJsonString(batch)
    )
}
