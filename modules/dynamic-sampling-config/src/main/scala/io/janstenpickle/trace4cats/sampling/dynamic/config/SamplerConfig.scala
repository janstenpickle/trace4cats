package io.janstenpickle.trace4cats.sampling.dynamic.config

sealed trait SamplerConfig
object SamplerConfig {
  case object Always extends SamplerConfig
  case object Never extends SamplerConfig
  case class Probabilistic(probability: Double, rootSpansOnly: Boolean = true) extends SamplerConfig
  case class Rate(bucketSize: Int, tokenRate: Double, rootSpansOnly: Boolean = true) extends SamplerConfig
}