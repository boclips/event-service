package com.boclips.event.aggregator.domain.model

import java.time.{Duration, LocalDate, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.events.VideoInteractedWithEvent

case class VideoId(value: String) extends Ordered[VideoId] {

  override def compare(that: VideoId): Int = value.compare(that.value)
}

case class Video(
                  id: VideoId,
                  releasedOn: LocalDate,
                  ingestedAt: ZonedDateTime,
                  title: String,
                  channelId: ChannelId,
                  playbackProvider: String,
                  subjects: List[Subject],
                  contentType: Option[String],
                  originalDimensions: Option[Dimensions],
                  assets: List[VideoAsset],
                  ageRange: AgeRange,
                  duration: Duration
                ) {

  def monthlyStorageCostGbp(): Double = {
    val totalSizeKb = assets.map(asset => asset.sizeKb).sum
    KalturaStorageCosts.monthlyStorageCostGbp(totalSizeKb)
  }

  def storageCostSoFarGbp(): Double = {
    val daysPassed = LocalDate.now().toEpochDay - ingestedAt.toLocalDate.toEpochDay
    val averageDaysPerMonth = 365.25 / 12
    val months = daysPassed / averageDaysPerMonth
    monthlyStorageCostGbp() * months
  }

  def storageCharges(to: LocalDate): List[VideoStorageCharge] = {
    if (assets.isEmpty) Nil else storageCharges(ingestedAt.toLocalDate, to, monthlyStorageCostGbp())
  }

  private def storageCharges(from: LocalDate, to: LocalDate, monthlyCostGbp: Double): List[VideoStorageCharge] = {
    if (from.withDayOfMonth(1) == to.withDayOfMonth(1)) {
      val cost = (to.getDayOfMonth - from.getDayOfMonth + 1.0) / from.lengthOfMonth() * monthlyCostGbp
      VideoStorageCharge(id, from, to, cost) :: Nil
    } else {
      val lastDayOfFirstMonth = from.withDayOfMonth(from.lengthOfMonth())
      val firstDayOfNextMonth = lastDayOfFirstMonth.plusDays(1)
      storageCharges(from, lastDayOfFirstMonth, monthlyCostGbp) ++ storageCharges(firstDayOfNextMonth, to, monthlyCostGbp)
    }
  }

  def largestAsset(): Option[VideoAsset] = {
    if (assets.nonEmpty) {
      return Some(assets.maxBy(asset => asset.sizeKb))
    }
    None
  }
}

case class Dimensions(width: Int, height: Int)

case class VideoAsset(
                       sizeKb: Int,
                       dimensions: Dimensions,
                       bitrateKbps: Int
                     )

case class VideoWithRelatedData(
                                 video: Video,
                                 playbacks: List[PlaybackWithRelatedData] = Nil,
                                 orders: List[VideoItemWithOrder] = Nil,
                                 channel: Option[Channel] = None,
                                 contract: Option[Contract] = None,
                                 impressions: List[VideoSearchResultImpression] = Nil,
                                 interactions: List[VideoInteractedWithEvent] = Nil
                               )

