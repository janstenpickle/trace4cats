lazy val commonSettings = Seq(
  scalaVersion := Dependencies.Versions.scala213,
  organization := "io.janstenpickle",
  organizationName := "janstenpickle",
  developers := List(
    Developer(
      "janstenpickle",
      "Chris Jansen",
      "janstenpickle@users.noreply.github.com",
      url = url("https://github.com/janstepickle")
    )
  ),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  homepage := Some(url("https://github.com/janstenpickle/trace4cats")),
  scmInfo := Some(
    ScmInfo(url("https://github.com/janstenpickle/trace4cats"), "scm:git:git@github.com:janstenpickle/trace4cats.git")
  ),
  javacOptions in (Compile, compile) ++= Seq("-source", "1.8", "-target", "1.8"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.patch)),
  libraryDependencies ++= Seq(Dependencies.cats, Dependencies.collectionCompat),
  bintrayRepository := "trace4cats",
  releaseEarlyWith in Global := SonatypePublisher,
  credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"),
  releaseEarlyEnableSyncToMaven := true,
  pgpPublicRing := file("./.github/git adlocal.pubring.asc"),
  pgpSecretRing := file("./.github/local.secring.asc"),
  crossScalaVersions := Seq(Dependencies.Versions.scala213, Dependencies.Versions.scala212),
  resolvers += Resolver.sonatypeRepo("releases")
)

lazy val noPublishSettings = commonSettings ++ Seq(publish := {}, publishArtifact := false, publishTo := None)

lazy val publishSettings = commonSettings ++ Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ =>
    false
  },
  publishArtifact in Test := false
)

lazy val graalSettings = Seq(
  graalVMNativeImageOptions ++= Seq(
    "--verbose",
    "--no-server",
    "--no-fallback",
    "--enable-http",
    "--enable-https",
    "--enable-all-security-services",
    "--report-unsupported-elements-at-runtime",
    "--allow-incomplete-classpath",
    "-Djava.net.preferIPv4Stack=true",
    "-H:IncludeResources='.*'",
    "-H:+ReportExceptionStackTraces",
    "-H:+ReportUnsupportedElementsAtRuntime",
    "-H:+TraceClassInitialization",
    "-H:+PrintClassInitialization",
    "-H:+RemoveSaturatedTypeFlows",
    "-H:+StackTrace",
    "-H:+JNI",
    "-H:-SpawnIsolates",
    "-H:-UseServiceLoaderFeature",
    "-H:ConfigurationFileDirectories=../../native-image/",
    "--install-exit-handlers",
    "--initialize-at-build-time=scala.runtime.Statics$VM",
    "--initialize-at-build-time=sun.instrument.InstrumentationImpl",
    "--initialize-at-build-time=scala.Symbol$",
    "--initialize-at-build-time=ch.qos.logback",
    "--initialize-at-build-time=org.slf4j.impl.StaticLoggerBinder",
    "--initialize-at-build-time=org.slf4j.LoggerFactory",
    "--initialize-at-build-time=org.apache.kafka,net.jpountz",
    "--initialize-at-build-time=com.github.luben.zstd.ZstdInputStream",
    "--initialize-at-build-time=com.github.luben.zstd.ZstdOutputStream",
    "--initialize-at-run-time=com.sun.management.internal.Flag",
    "--initialize-at-run-time=com.sun.management.internal.OperatingSystemImpl"
  )
)

lazy val root = (project in file("."))
  .settings(noPublishSettings)
  .settings(name := "Trace4Cats")
  .aggregate(
    model,
    core,
    kernel,
    avro,
    inject,
    `inject-zio`,
    fs2,
    `http4s-common`,
    `http4s-client`,
    `http4s-server`,
    `avro-exporter`,
    `avro-kafka-exporter`,
    `avro-server`,
    `avro-kafka-consumer`,
    `avro-test`,
    `collector-common`,
    `datadog-http-exporter`,
    `exporter-stream`,
    `exporter-common`,
    `exporter-http`,
    `log-exporter`,
    `jaeger-thrift-exporter`,
    `newrelic-http-exporter`,
    `opentelemetry-common`,
    `opentelemetry-jaeger-exporter`,
    `opentelemetry-otlp-grpc-exporter`,
    `opentelemetry-otlp-http-exporter`,
    `stackdriver-common`,
    `stackdriver-grpc-exporter`,
    `stackdriver-http-exporter`,
    `sttp-client`,
    `kafka-client`,
    `graal-kafka`,
    natchez,
    `tail-sampling`,
    `tail-sampling-cache-store`,
    `tail-sampling-redis-store`,
    filtering
  )

lazy val model =
  (project in file("modules/model"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-model",
      libraryDependencies ++= Seq(
        Dependencies.enumeratum,
        Dependencies.enumeratumCats,
        Dependencies.commonsCodec,
        Dependencies.kittens
      )
    )

lazy val example = (project in file("modules/example"))
  .settings(noPublishSettings)
  .settings(
    name := "trace4cats-example",
    libraryDependencies ++= Seq(
      Dependencies.catsEffect,
      Dependencies.logback,
      Dependencies.http4sBlazeClient,
      Dependencies.http4sBlazeServer,
      Dependencies.http4sDsl,
      Dependencies.sttpHttp4s
    )
  )
  .dependsOn(
    model,
    kernel,
    core,
    inject,
    `inject-zio`,
    fs2,
    `http4s-client`,
    `http4s-server`,
    natchez,
    `avro-exporter`,
    `log-exporter`,
    `jaeger-thrift-exporter`,
    `opentelemetry-jaeger-exporter`,
    `opentelemetry-otlp-grpc-exporter`,
    `opentelemetry-otlp-http-exporter`,
    `stackdriver-grpc-exporter`,
    `stackdriver-http-exporter`,
    `sttp-client`,
    `tail-sampling`,
    `tail-sampling-cache-store`,
    filtering
  )

lazy val test = (project in file("modules/test"))
  .settings(noPublishSettings)
  .settings(name := "trace4cats-test", libraryDependencies ++= Dependencies.test ++ Seq(Dependencies.fs2))
  .dependsOn(model)

lazy val `avro-test` = (project in file("modules/avro-test"))
  .settings(noPublishSettings)
  .settings(
    name := "trace4cats-avro-test",
    libraryDependencies ++= Dependencies.test.map(_ % Test),
    libraryDependencies ++= Seq(Dependencies.logback % Test)
  )
  .dependsOn(model)
  .dependsOn(`avro-exporter`, `avro-server`, test % "test->compile")

lazy val kernel =
  (project in file("modules/kernel"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-kernel",
      libraryDependencies ++= Dependencies.test.map(_ % Test),
      libraryDependencies ++= Seq(Dependencies.catsEffect % Test)
    )
    .dependsOn(model, test % "test->compile")

lazy val core =
  (project in file("modules/core"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-core",
      libraryDependencies ++= Dependencies.test.map(_ % Test),
      libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.catsEffectLaws % Test)
    )
    .dependsOn(model, kernel, test % "test->compile", `exporter-common` % "test->compile")

lazy val avro =
  (project in file("modules/avro"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-avro",
      libraryDependencies ++= Seq(Dependencies.vulcan, Dependencies.vulcanGeneric, Dependencies.vulcanEnumeratum)
    )
    .dependsOn(model)

lazy val `log-exporter` =
  (project in file("modules/log-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-log-exporter",
      libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.log4cats)
    )
    .dependsOn(model, kernel)

lazy val `jaeger-integration-test` =
  (project in file("modules/jaeger-integration-test"))
    .settings(noPublishSettings)
    .settings(
      name := "trace4cats-jaeger-integration-test",
      libraryDependencies ++= Dependencies.test,
      libraryDependencies ++= Seq(
        Dependencies.circeGeneric,
        Dependencies.http4sCirce,
        Dependencies.http4sBlazeClient,
        Dependencies.logback,
        Dependencies.testContainers
      )
    )
    .dependsOn(kernel, test)

lazy val `jaeger-thrift-exporter` =
  (project in file("modules/jaeger-thrift-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-jaeger-thrift-exporter",
      libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.fs2, Dependencies.jaegerThrift)
    )
    .dependsOn(model, kernel, `exporter-common`, `jaeger-integration-test` % "test->compile")

lazy val `opentelemetry-common` =
  (project in file("modules/opentelemetry-common"))
    .settings(publishSettings)
    .settings(commonSettings)
    .settings(
      name := "trace4cats-opentelemetry-common",
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.fs2,
        Dependencies.openTelemetrySdk,
        Dependencies.grpcApi
      )
    )
    .dependsOn(model, kernel, `exporter-common`)

lazy val `opentelemetry-jaeger-exporter` =
  (project in file("modules/opentelemetry-jaeger-exporter"))
    .settings(publishSettings)
    .settings(commonSettings)
    .settings(
      name := "trace4cats-opentelemetry-jaeger-exporter",
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.fs2,
        Dependencies.openTelemetryJaegerExporter,
        Dependencies.grpcOkHttp % Test
      )
    )
    .dependsOn(model, kernel, `exporter-common`, `opentelemetry-common`, `jaeger-integration-test` % "test->compile")

lazy val `opentelemetry-otlp-grpc-exporter` =
  (project in file("modules/opentelemetry-otlp-grpc-exporter"))
    .settings(publishSettings)
    .settings(commonSettings)
    .settings(
      name := "trace4cats-opentelemetry-otlp-grpc-exporter",
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.fs2,
        Dependencies.openTelemetryOtlpExporter,
        Dependencies.grpcOkHttp % Test
      )
    )
    .dependsOn(model, kernel, `exporter-common`, `opentelemetry-common`, `jaeger-integration-test` % "test->compile")

lazy val `opentelemetry-otlp-http-exporter` =
  (project in file("modules/opentelemetry-otlp-http-exporter"))
    .settings(publishSettings)
    .settings(commonSettings)
    .settings(
      name := "trace4cats-opentelemetry-otlp-http-exporter",
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.circeGeneric,
        Dependencies.fs2,
        Dependencies.http4sClient,
        Dependencies.http4sBlazeClient,
        (Dependencies.openTelemetryProto % "protobuf").intransitive(),
        Dependencies.scalapbJson
      ),
      PB.protoSources in Compile += target.value / "protobuf_external",
      PB.targets in Compile := Seq(scalapb.gen(grpc = false, lenses = false) -> (sourceManaged in Compile).value)
    )
    .dependsOn(model, kernel, `exporter-common`, `exporter-http`, `jaeger-integration-test` % "test->compile")

lazy val `stackdriver-common` =
  (project in file("modules/stackdriver-common"))
    .settings(publishSettings)
    .settings(name := "trace4cats-stackdriver-common")

lazy val `stackdriver-grpc-exporter` =
  (project in file("modules/stackdriver-grpc-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-stackdriver-grpc-exporter",
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.fs2,
        Dependencies.googleCredentials,
        Dependencies.googleCloudTrace
      )
    )
    .dependsOn(model, kernel, `exporter-common`, `stackdriver-common`)

lazy val `stackdriver-http-exporter` =
  (project in file("modules/stackdriver-http-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-stackdriver-http-exporter",
      libraryDependencies ++= Dependencies.test.map(_ % Test),
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.circeGeneric,
        Dependencies.circeParser,
        Dependencies.enumeratumCirce,
        Dependencies.fs2,
        Dependencies.http4sClient,
        Dependencies.http4sCirce,
        Dependencies.http4sBlazeClient,
        Dependencies.jwt,
        Dependencies.log4cats
      )
    )
    .dependsOn(model, kernel, `exporter-common`, `exporter-http`, `stackdriver-common`)

lazy val `datadog-http-exporter` =
  (project in file("modules/datadog-http-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-datadog-http-exporter",
      libraryDependencies ++= Dependencies.test.map(_ % Test),
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.circeGeneric,
        Dependencies.circeParser,
        Dependencies.fs2,
        Dependencies.http4sClient,
        Dependencies.http4sCirce,
        Dependencies.http4sBlazeClient
      )
    )
    .dependsOn(model, kernel, `exporter-common`, `exporter-http`, test % "test->compile")

lazy val `newrelic-http-exporter` =
  (project in file("modules/newrelic-http-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-newrelic-http-exporter",
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.circeGeneric,
        Dependencies.circeParser,
        Dependencies.fs2,
        Dependencies.http4sClient,
        Dependencies.http4sCirce,
        Dependencies.http4sBlazeClient
      )
    )
    .dependsOn(model, kernel, `exporter-common`, `exporter-http`)

lazy val `avro-kafka-exporter` =
  (project in file("modules/avro-kafka-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-avro-kafka-exporter",
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.fs2,
        Dependencies.fs2Kafka,
        Dependencies.kafka,
        Dependencies.log4cats
      ),
      libraryDependencies ++= (Dependencies.test ++ Seq(Dependencies.embeddedKafka)).map(_ % Test),
      classLoaderLayeringStrategy in Test := ClassLoaderLayeringStrategy.ScalaLibrary,
      classLoaderLayeringStrategy in Test := ClassLoaderLayeringStrategy.Flat
    )
    .dependsOn(model, kernel, `exporter-common`, avro, test % "test->compile")

lazy val `exporter-stream` =
  (project in file("modules/exporter-stream"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-exporter-stream",
      libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.fs2)
    )
    .dependsOn(model, kernel)

lazy val `exporter-common` =
  (project in file("modules/exporter-common"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-exporter-common",
      libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.fs2, Dependencies.log4cats)
    )
    .dependsOn(model, kernel, `exporter-stream`)

lazy val `exporter-http` =
  (project in file("modules/exporter-http"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-exporter-http",
      libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.fs2, Dependencies.http4sClient)
    )
    .dependsOn(model, kernel)

lazy val `avro-exporter` =
  (project in file("modules/avro-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-avro-exporter",
      libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.fs2, Dependencies.fs2Io)
    )
    .dependsOn(model, kernel, avro, `exporter-common`)

lazy val `avro-server` =
  (project in file("modules/avro-server"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-avro-server",
      libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.fs2, Dependencies.fs2Io, Dependencies.log4cats)
    )
    .dependsOn(model, avro)

lazy val `avro-kafka-consumer` =
  (project in file("modules/avro-kafka-consumer"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-avro-kafka-consumer",
      libraryDependencies ++= Seq(
        Dependencies.catsEffect,
        Dependencies.fs2,
        Dependencies.fs2Kafka,
        Dependencies.kafka,
        Dependencies.log4cats
      ),
      libraryDependencies ++= Seq(Dependencies.embeddedKafka, Dependencies.logback).map(_ % Test)
    )
    .dependsOn(model, avro, test % "test->compile")

lazy val inject = (project in file("modules/inject"))
  .settings(publishSettings)
  .settings(name := "trace4cats-inject")
  .dependsOn(model, kernel, core)

lazy val `inject-zio` = (project in file("modules/inject-zio"))
  .settings(publishSettings)
  .settings(name := "trace4cats-inject-zio", libraryDependencies ++= Seq(Dependencies.zioInterop, Dependencies.catsMtl))
  .dependsOn(inject)

lazy val fs2 = (project in file("modules/fs2"))
  .settings(publishSettings)
  .settings(name := "trace4cats-fs2", libraryDependencies ++= Seq(Dependencies.fs2))
  .dependsOn(model, kernel, core, inject)

lazy val `kafka-client` = (project in file("modules/kafka-client"))
  .settings(publishSettings)
  .settings(name := "trace4cats-kafka-client", libraryDependencies ++= Seq(Dependencies.catsMtl, Dependencies.fs2Kafka))
  .dependsOn(model, kernel, core, inject, fs2, `exporter-common` % "test->compile")

lazy val `http4s-common` = (project in file("modules/http4s-common"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-http4s-common",
    libraryDependencies ++= Seq(Dependencies.http4sServer, Dependencies.http4sDsl)
  )
  .dependsOn(model)

lazy val `sttp-client` = (project in file("modules/sttp-client"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-sttp-client",
    libraryDependencies ++= Seq(Dependencies.catsMtl, Dependencies.sttpClient),
    libraryDependencies ++= (Dependencies.test ++ Seq(
      Dependencies.http4sBlazeClient,
      Dependencies.http4sBlazeServer,
      Dependencies.http4sDsl,
      Dependencies.sttpHttp4s
    )).map(_ % Test)
  )
  .dependsOn(model, kernel, core, inject, `exporter-common` % "test->compile")

lazy val `http4s-client` = (project in file("modules/http4s-client"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-http4s-client",
    libraryDependencies ++= Seq(Dependencies.catsMtl, Dependencies.http4sClient),
    libraryDependencies ++= (Dependencies.test ++ Seq(
      Dependencies.http4sBlazeClient,
      Dependencies.http4sBlazeServer,
      Dependencies.http4sDsl
    )).map(_ % Test)
  )
  .dependsOn(model, kernel, core, inject, `http4s-common`, `exporter-common` % "test->compile")

lazy val `http4s-server` = (project in file("modules/http4s-server"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-http4s-server",
    libraryDependencies ++= Seq(Dependencies.http4sServer),
    libraryDependencies ++= (Dependencies.test ++ Seq(Dependencies.http4sBlazeClient, Dependencies.http4sBlazeServer))
      .map(_ % Test)
  )
  .dependsOn(model, kernel, core, inject, `http4s-common`, `exporter-common` % "test->compile")

lazy val natchez = (project in file("modules/natchez"))
  .settings(publishSettings)
  .settings(name := "trace4cats-natchez", libraryDependencies ++= Seq(Dependencies.natchez))
  .dependsOn(model, kernel, core, inject)

lazy val `graal-kafka` = (project in file("modules/graal-kafka"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-graal-kafka",
    libraryDependencies ++= Seq(Dependencies.svm, Dependencies.kafka, Dependencies.micronautCore)
  )

lazy val agent = (project in file("modules/agent"))
  .settings(noPublishSettings)
  .settings(graalSettings)
  .settings(
    name := "trace4cats-agent",
    libraryDependencies ++= Seq(
      Dependencies.catsEffect,
      Dependencies.declineEffect,
      Dependencies.log4cats,
      Dependencies.logback
    )
  )
  .dependsOn(model, `avro-exporter`, `avro-server`, `exporter-common`)
  .enablePlugins(GraalVMNativeImagePlugin)

lazy val `agent-kafka` = (project in file("modules/agent-kafka"))
  .settings(noPublishSettings)
  .settings(graalSettings)
  .settings(
    name := "trace4cats-agent-kafka",
    libraryDependencies ++= Seq(
      Dependencies.catsEffect,
      Dependencies.declineEffect,
      Dependencies.log4cats,
      Dependencies.logback
    )
  )
  .dependsOn(model, `avro-exporter`, `avro-kafka-exporter`, `avro-server`, `exporter-common`, `graal-kafka`)
  .enablePlugins(GraalVMNativeImagePlugin)

lazy val filtering = (project in file("modules/filtering"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-filtering",
    libraryDependencies ++= Seq(Dependencies.cats, Dependencies.fs2),
    libraryDependencies ++= Dependencies.test.map(_ % Test)
  )
  .dependsOn(model, kernel, `exporter-stream`)

lazy val `tail-sampling` = (project in file("modules/tail-sampling"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-tail-sampling",
    libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.log4cats)
  )
  .dependsOn(model, kernel, `exporter-stream`)

lazy val `tail-sampling-cache-store` = (project in file("modules/tail-sampling-cache-store"))
  .settings(publishSettings)
  .settings(name := "trace4cats-tail-sampling-cache-store", libraryDependencies ++= Seq(Dependencies.scaffeine))
  .dependsOn(`tail-sampling`)

lazy val `tail-sampling-redis-store` = (project in file("modules/tail-sampling-redis-store"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-tail-sampling-redis-store",
    libraryDependencies ++= Seq(Dependencies.redis4cats, Dependencies.redis4catsLog4cats, Dependencies.scaffeine)
  )
  .dependsOn(`tail-sampling`)

lazy val `collector-common` = (project in file("modules/collector-common"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-collector-common",
    libraryDependencies ++= Seq(
      Dependencies.catsEffect,
      Dependencies.circeGeneric,
      Dependencies.circeYaml,
      Dependencies.declineEffect,
      Dependencies.fs2,
      Dependencies.http4sJdkClient,
      Dependencies.log4cats
    )
  )
  .dependsOn(
    model,
    `exporter-common`,
    `avro-exporter`,
    `avro-server`,
    `datadog-http-exporter`,
    `jaeger-thrift-exporter`,
    `log-exporter`,
    `opentelemetry-otlp-http-exporter`,
    `stackdriver-http-exporter`,
    `newrelic-http-exporter`,
    `avro-kafka-exporter`,
    `avro-kafka-consumer`,
    `tail-sampling`,
    `tail-sampling-cache-store`,
    `tail-sampling-redis-store`,
    filtering
  )

lazy val collector = (project in file("modules/collector"))
  .settings(noPublishSettings)
  .settings(
    name := "trace4cats-collector",
    dockerRepository := Some("janstenpickle"),
    dockerUpdateLatest := true,
    dockerBaseImage := "openjdk:13",
    dockerExposedPorts += 7777,
    dockerExposedUdpPorts += 7777,
    daemonUserUid in Docker := Some("9000"),
    javaOptions in Universal ++= Seq("-Djava.net.preferIPv4Stack=true"),
    libraryDependencies ++= Seq(
      Dependencies.catsEffect,
      Dependencies.declineEffect,
      Dependencies.fs2,
      Dependencies.grpcOkHttp,
      Dependencies.log4cats,
      Dependencies.logback
    )
  )
  .dependsOn(
    model,
    `collector-common`,
    `exporter-common`,
    `avro-exporter`,
    `avro-server`,
    `datadog-http-exporter`,
    `jaeger-thrift-exporter`,
    `log-exporter`,
    `opentelemetry-jaeger-exporter`,
    `opentelemetry-otlp-grpc-exporter`,
    `opentelemetry-otlp-http-exporter`,
    `stackdriver-grpc-exporter`,
    `stackdriver-http-exporter`
  )
  .enablePlugins(UniversalPlugin, JavaAppPackaging, DockerPlugin)

lazy val `collector-lite` = (project in file("modules/collector-lite"))
  .settings(noPublishSettings)
  .settings(graalSettings)
  .settings(
    name := "trace4cats-collector-lite",
    libraryDependencies ++= Seq(
      Dependencies.catsEffect,
      Dependencies.declineEffect,
      Dependencies.fs2,
      Dependencies.log4cats,
      Dependencies.logback
    )
  )
  .dependsOn(
    model,
    `exporter-common`,
    `collector-common`,
    `avro-exporter`,
    `avro-server`,
    `datadog-http-exporter`,
    `jaeger-thrift-exporter`,
    `log-exporter`,
    `opentelemetry-otlp-http-exporter`,
    `stackdriver-http-exporter`,
    `graal-kafka`
  )
  .enablePlugins(GraalVMNativeImagePlugin)
