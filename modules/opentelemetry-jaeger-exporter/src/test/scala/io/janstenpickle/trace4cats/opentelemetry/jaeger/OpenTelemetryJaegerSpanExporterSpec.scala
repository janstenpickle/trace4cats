package io.janstenpickle.trace4cats.opentelemetry.jaeger

import java.time.Instant

import cats.effect.IO
import fs2.Chunk
import io.janstenpickle.trace4cats.`export`.SemanticTags
import io.janstenpickle.trace4cats.model.{Batch, TraceProcess}
import io.janstenpickle.trace4cats.test.jaeger.BaseJaegerSpec

class OpenTelemetryJaegerSpanExporterSpec extends BaseJaegerSpec {
  it should "Send a batch of spans to jaeger" in forAll { (batch: Batch[Chunk], process: TraceProcess) =>
    val updatedBatch =
      Batch(
        batch.spans.map(
          span =>
            span.copy(
              serviceName = process.serviceName,
              attributes = process.attributes ++ span.attributes,
              start = Instant.now(),
              end = Instant.now()
            )
        )
      )

    testExporter(
      OpenTelemetryJaegerSpanExporter[IO, Chunk]("localhost", 14250, process.serviceName),
      updatedBatch,
      batchToJaegerResponse(
        updatedBatch,
        process,
        SemanticTags.kindTags,
        SemanticTags.statusTags("span.", statusCode),
        Map("otel.library.name" -> "trace4cats")
      )
    )
  }
}
