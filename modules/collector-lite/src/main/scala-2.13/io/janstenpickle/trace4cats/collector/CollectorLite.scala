package io.janstenpickle.trace4cats.collector

import cats.effect.{ExitCode, IO}
import com.monovore.decline._
import com.monovore.decline.effect._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.janstenpickle.trace4cats.collector.common.CommonCollector
import io.janstenpickle.trace4cats.kernel.BuildInfo
import cats.effect.Resource

object CollectorLite
    extends CommandIOApp(
      name = "trace4cats-collector-lite",
      header = "Trace 4 Cats Collector Lite Edition",
      version = BuildInfo.version
    ) {

  override def main: Opts[IO[ExitCode]] =
    CommonCollector.configFileOpt.map { configFile =>
      Slf4jLogger.create[IO].flatMap { implicit logger =>
        (for {
          blocker <- Resource.unit[IO]
          stream <- CommonCollector[IO](blocker, configFile, List.empty)
        } yield stream).use(_.compile.drain.as(ExitCode.Success)).handleErrorWith { th =>
          logger.error(th)("Trace 4 Cats collector failed").as(ExitCode.Error)
        }
      }
    }

}
