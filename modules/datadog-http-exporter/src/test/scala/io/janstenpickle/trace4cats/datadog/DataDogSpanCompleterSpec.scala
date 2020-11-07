package io.janstenpickle.trace4cats.datadog

import cats.effect.{Blocker, IO}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.janstenpickle.trace4cats.model.{CompletedSpan, TraceProcess}
import io.janstenpickle.trace4cats.test.ArbitraryInstances
import org.scalacheck.Shrink
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import scala.concurrent.ExecutionContext

import scala.concurrent.duration._

class DataDogSpanCompleterSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks with ArbitraryInstances {
  implicit val contextShift = IO.contextShift(ExecutionContext.global)
  implicit val timer = IO.timer(ExecutionContext.global)

  val blocker = Blocker.liftExecutionContext(ExecutionContext.global)

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  implicit override val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSuccessful = 3, maxDiscardedFactor = 50.0)

  implicit def noShrink[T]: Shrink[T] = Shrink.shrinkAny

  behavior.of("DataDogSpanCompleter")

  it should "send a span to datadog agent without error" in forAll {
    (process: TraceProcess, span: CompletedSpan.Builder) =>
      assertResult(())(
        DataDogSpanCompleter
          .blazeClient[IO](blocker, process, batchTimeout = 100.millis)
          .use(_.complete(span))
          .unsafeRunSync()
      )
  }
}
