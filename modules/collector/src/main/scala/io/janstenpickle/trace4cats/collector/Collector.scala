package io.janstenpickle.trace4cats.collector

import cats.Parallel
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, ExitCode, IO, Resource, Timer}
import cats.implicits._
import com.monovore.decline._
import com.monovore.decline.effect._
import fs2.Chunk
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.janstenpickle.trace4cats.kernel.SpanExporter
import io.janstenpickle.trace4cats.collector.common.CommonCollector
import io.janstenpickle.trace4cats.collector.common.config.ConfigParser
import io.janstenpickle.trace4cats.collector.config.CollectorConfig
import io.janstenpickle.trace4cats.opentelemetry.jaeger.OpenTelemetryJaegerSpanExporter
import io.janstenpickle.trace4cats.opentelemetry.otlp.OpenTelemetryOtlpGrpcSpanExporter
import io.janstenpickle.trace4cats.stackdriver.StackdriverGrpcSpanExporter

object Collector
    extends CommandIOApp(name = "trace4cats-collector", header = "Trace 4 Cats Collector", version = "0.1.0") {

  override def main: Opts[IO[ExitCode]] =
    CommonCollector.configFileOpt.map { configFile =>
      Slf4jLogger.create[IO].flatMap { implicit logger =>
        (for {
          blocker <- Blocker[IO]
          oth <- others[IO](blocker, configFile)
          stream <- CommonCollector[IO](blocker, configFile, oth)
        } yield stream).use(_.compile.drain.as(ExitCode.Success)).handleErrorWith { th =>
          logger.error(th)("Trace 4 Cats collector failed").as(ExitCode.Error)
        }
      }
    }

  def others[F[_]: ConcurrentEffect: Parallel: ContextShift: Timer](
    blocker: Blocker,
    configFile: String
  ): Resource[F, List[(String, SpanExporter[F, Chunk])]] =
    for {
      config <- Resource.liftF(ConfigParser.parse[F, CollectorConfig](configFile))
      jaegerProtoExporter <- config.jaegerProto.traverse { jaeger =>
        OpenTelemetryJaegerSpanExporter[F, Chunk](jaeger.host, jaeger.port).map("Jaeger Proto" -> _)
      }

      otGrpcExporter <- config.otlpGrpc.traverse { otlp =>
        OpenTelemetryOtlpGrpcSpanExporter[F, Chunk](host = otlp.host, port = otlp.port).map("OpenTelemetry GRPC" -> _)
      }

      stackdriverExporter <- config.stackdriverGrpc.traverse { stackdriver =>
        StackdriverGrpcSpanExporter[F, Chunk](blocker, projectId = stackdriver.projectId).map("Stackdriver GRPC" -> _)
      }
    } yield List(jaegerProtoExporter, otGrpcExporter, stackdriverExporter).flatten

}
