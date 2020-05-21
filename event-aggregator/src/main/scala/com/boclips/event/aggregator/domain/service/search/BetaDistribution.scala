package com.boclips.event.aggregator.domain.service.search

case class BetaDistribution(alpha: Int, beta: Int) {

  def mean(): Double = alpha.toDouble / total

  def total(): Int = alpha + beta

  def add(success: Boolean): BetaDistribution = {
    if (success)
      BetaDistribution(alpha + 1, beta)
    else
      BetaDistribution(alpha, beta + 1)
  }

  def add(other: BetaDistribution): BetaDistribution = {
    BetaDistribution(alpha + other.alpha, beta + other.beta)
  }

}
