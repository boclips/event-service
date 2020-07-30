package com.boclips.event.aggregator.testsupport.testfactories

import java.time.{Duration, LocalDate, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.contentpartners.ChannelId
import com.boclips.event.aggregator.domain.model.{videos, _}
import com.boclips.event.aggregator.domain.model.videos.{Dimensions, Video, VideoAsset, VideoId, VideoStorageCharge, VideoTopic}

object VideoFactory {

  def createVideo(
                   id: String = "123",
                   releasedOn: LocalDate = LocalDate.now(),
                   ingestedAt: ZonedDateTime = ZonedDateTime.now(),
                   title: String = "title",
                   channelId: String = "channel id",
                   contentType: Option[String] = None,
                   subjects: List[String] = List("English"),
                   playbackProvider: String = "KALTURA",
                   assets: List[VideoAsset] = List(),
                   originalDimensions: Option[Dimensions] = None,
                   ageRange: AgeRange = AgeRange(Some(5), Some(7)),
                   duration: Duration = Duration.ofSeconds(180),
                   promoted: Boolean = false,
                   topics: List[VideoTopic] = List()
                 ): Video = {
    videos.Video(
      id = VideoId(id),
      releasedOn = releasedOn,
      ingestedAt = ingestedAt,
      title = title,
      contentType = contentType,
      channelId = ChannelId(channelId),
      playbackProvider = playbackProvider,
      assets = assets,
      originalDimensions = originalDimensions,
      subjects = subjects.map(name => Subject(name)),
      ageRange = ageRange,
      duration = duration,
      promoted = promoted,
      topics = topics
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
