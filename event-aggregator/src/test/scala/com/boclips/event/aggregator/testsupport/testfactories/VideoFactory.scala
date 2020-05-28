package com.boclips.event.aggregator.testsupport.testfactories

import java.time.{Duration, LocalDate, ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._

object VideoFactory {

  def createVideo(
                   id: String = "123",
                   ingestedAt: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                   title: String = "title",
                   channelId: String = "channel id",
                   contentType: Option[String] = None,
                   subjects: List[String] = List("English"),
                   playbackProvider: String = "KALTURA",
                   assets: List[VideoAsset] = List(),
                   originalDimensions: Option[Dimensions] = None,
                   ageRange: AgeRange = AgeRange(Some(5), Some(7)),
                   duration: Duration = Duration.ofSeconds(180)
                 ): Video = {
    Video(
      id = VideoId(id),
      ingestedAt = ingestedAt,
      title = title,
      contentType = contentType,
      channelId = ChannelId(channelId),
      playbackProvider = playbackProvider,
      assets = assets,
      originalDimensions = originalDimensions,
      subjects = subjects.map(name => Subject(name)),
      ageRange = ageRange,
      duration = duration
    )
  }

  def createVideoAsset(
                        sizeKb: Int = 1000,
                        dimensions: Dimensions = Dimensions(1920, 1080),
                        bitrateKbps: Int = 100
                      ): VideoAsset = {
    VideoAsset(
      sizeKb = sizeKb,
      dimensions = dimensions,
      bitrateKbps = bitrateKbps
    )
  }

  def createStorageCharge(
                           videoId: VideoId = VideoId("video-id"),
                           periodStart: LocalDate = LocalDate.now(),
                           periodEnd: LocalDate = LocalDate.now(),
                           valueGbp: Double = 1
                         ): VideoStorageCharge = {
    VideoStorageCharge(
      videoId = videoId,
      periodStart = periodStart,
      periodEnd = periodEnd,
      valueGbp = valueGbp,
    )
  }
}
