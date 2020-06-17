package com.boclips.event.aggregator.domain.model.videos

object KalturaStorageCosts {
  final val GB_MONTHLY_GBP = 0.1
  final val UNIT_MULTIPLIER = 1000.0

  def monthlyStorageCostGbp(sizeKb: Int): Double = {
    val sizeGb = sizeKb / KalturaStorageCosts.UNIT_MULTIPLIER / KalturaStorageCosts.UNIT_MULTIPLIER
    sizeGb * KalturaStorageCosts.GB_MONTHLY_GBP
  }
}
