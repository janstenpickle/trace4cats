import sbt._

object Dependencies {
  object Versions {
    val cats = "2.1.1"
    val catsEffect = "2.1.4"
    val collectionCompat = "2.1.6"
    val commonsCodec = "1.14"
    val circe = "0.13.0"
    val decline = "1.3.0"
    val enumeratum = "1.6.1"
    val fs2 = "2.4.4"
    val googleCredentials = "0.21.1"
    val googleCloudTrace = "1.2.0"
    val grpc = "1.31.1"
    val http4s = "0.21.6"
    val http4sJdkClient = "0.3.1"
    val jaeger = "1.3.2"
    val jwt = "3.10.3"
    val kittens = "2.1.0"
    val log4cats = "1.1.1"
    val logback = "1.2.3"
    val natchez = "0.0.12"
    val openTelemetry = "0.8.0"
    val scala212 = "2.12.11"
    val scala213 = "2.13.3"
    val scalapb = "0.10.1"
    val vulcan = "1.1.0"

    val disciplineScalatest = "2.0.1"
    val discipline = "1.0.3"
    val scalaCheck = "1.14.3"
    val scalaCheckShapeless = "1.2.5"
    val scalaTest = "3.2.2"
    val testContainers = "0.37.0"
  }

  lazy val cats = "org.typelevel"                           %% "cats-core"                      % Versions.cats
  lazy val catsEffect = "org.typelevel"                     %% "cats-effect"                    % Versions.catsEffect
  lazy val commonsCodec = "commons-codec"                   % "commons-codec"                   % Versions.commonsCodec
  lazy val collectionCompat = "org.scala-lang.modules"      %% "scala-collection-compat"        % Versions.collectionCompat
  lazy val circeGeneric = "io.circe"                        %% "circe-generic-extras"           % Versions.circe
  lazy val circeParser = "io.circe"                         %% "circe-parser"                   % Versions.circe
  lazy val enumeratum = "com.beachape"                      %% "enumeratum"                     % Versions.enumeratum
  lazy val enumeratumCats = "com.beachape"                  %% "enumeratum-cats"                % Versions.enumeratum
  lazy val enumeratumCirce = "com.beachape"                 %% "enumeratum-circe"               % Versions.enumeratum
  lazy val declineEffect = "com.monovore"                   %% "decline-effect"                 % Versions.decline
  lazy val googleCredentials = "com.google.auth"            % "google-auth-library-credentials" % Versions.googleCredentials
  lazy val googleCloudTrace = "com.google.cloud"            % "google-cloud-trace"              % Versions.googleCloudTrace
  lazy val fs2 = "co.fs2"                                   %% "fs2-core"                       % Versions.fs2
  lazy val fs2Io = "co.fs2"                                 %% "fs2-io"                         % Versions.fs2
  lazy val grpcOkHttp = "io.grpc"                           % "grpc-okhttp"                     % Versions.grpc
  lazy val grpcApi = "io.grpc"                              % "grpc-api"                        % Versions.grpc
  lazy val http4sClient = "org.http4s"                      %% "http4s-client"                  % Versions.http4s
  lazy val http4sCirce = "org.http4s"                       %% "http4s-circe"                   % Versions.http4s
  lazy val http4sCore = "org.http4s"                        %% "http4s-core"                    % Versions.http4s
  lazy val http4sDsl = "org.http4s"                         %% "http4s-dsl"                     % Versions.http4s
  lazy val http4sEmberClient = "org.http4s"                 %% "http4s-ember-client"            % Versions.http4s
  lazy val http4sEmberServer = "org.http4s"                 %% "http4s-ember-server"            % Versions.http4s
  lazy val http4sJdkClient = "org.http4s"                   %% "http4s-jdk-http-client"         % Versions.http4sJdkClient
  lazy val http4sServer = "org.http4s"                      %% "http4s-server"                  % Versions.http4s
  lazy val jaegerThrift = "io.jaegertracing"                % "jaeger-thrift"                   % Versions.jaeger
  lazy val jwt = "com.auth0"                                % "java-jwt"                        % Versions.jwt
  lazy val kittens = "org.typelevel"                        %% "kittens"                        % Versions.kittens
  lazy val log4cats = "io.chrisdavenport"                   %% "log4cats-slf4j"                 % Versions.log4cats
  lazy val logback = "ch.qos.logback"                       % "logback-classic"                 % Versions.logback
  lazy val natchez = "org.tpolecat"                         %% "natchez-core"                   % Versions.natchez
  lazy val openTelemetrySdk = "io.opentelemetry"            % "opentelemetry-sdk"               % Versions.openTelemetry
  lazy val openTelemetryOtlpExporter = "io.opentelemetry"   % "opentelemetry-exporters-otlp"    % Versions.openTelemetry
  lazy val openTelemetryJaegerExporter = "io.opentelemetry" % "opentelemetry-exporters-jaeger"  % Versions.openTelemetry
  lazy val openTelemetryProto = "io.opentelemetry"          % "opentelemetry-proto"             % Versions.openTelemetry
  lazy val scalapbJson = "com.thesamet.scalapb"             %% "scalapb-json4s"                 % Versions.scalapb
  lazy val vulcan = "com.github.fd4s"                       %% "vulcan"                         % Versions.vulcan
  lazy val vulcanGeneric = "com.github.fd4s"                %% "vulcan-generic"                 % Versions.vulcan
  lazy val vulcanEnumeratum = "com.github.fd4s"             %% "vulcan-enumeratum"              % Versions.vulcan

  lazy val catsLaws = "org.typelevel"                         %% "cats-laws"                      % Versions.cats
  lazy val catsEffectLaws = "org.typelevel"                   %% "cats-effect-laws"               % Versions.catsEffect
  lazy val disciplineScalatest = "org.typelevel"              %% "discipline-scalatest"           % Versions.disciplineScalatest
  lazy val disciplineCore = "org.typelevel"                   %% "discipline-core"                % Versions.discipline
  lazy val scalacheck = "org.scalacheck"                      %% "scalacheck"                     % Versions.scalaCheck
  lazy val scalacheckShapeless = "com.github.alexarchambault" %% "scalacheck-shapeless_1.14"      % Versions.scalaCheckShapeless
  lazy val scalaTest = "org.scalatest"                        %% "scalatest"                      % Versions.scalaTest
  lazy val testContainers = "com.dimafeng"                    %% "testcontainers-scala-scalatest" % Versions.testContainers

  lazy val test = Seq(catsLaws, disciplineScalatest, disciplineCore, scalacheck, scalacheckShapeless, scalaTest)
}
