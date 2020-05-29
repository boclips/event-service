package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{Duration, LocalDate, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.infrastructure.video.{VideoAssetDocument, VideoDocument}

import scala.collection.JavaConverters._

object DocumentToVideoConverter {

  def convert(document: VideoDocument): Video = {
    Video(
      id = VideoId(document.getId()),
      title = document.getTitle,
      channelId = ChannelId(document.getChannelId),
      playbackProvider = document.getPlaybackProviderType,
      contentType = Option(document.getType),
      subjects = document.getSubjects.asScala.map(name => Subject(name)).toList,
      duration = Duration.ofSeconds(document.getDurationSeconds.toLong),
      releasedOn = LocalDate.parse(document.getReleasedOn),
      ingestedAt = ZonedDateTime.parse(document.getIngestedAt),
      assets = document.getAssets match {
        case null => List()
        case assetDocuments => assetDocuments.asScala.map(convertAsset).toList
      },
      originalDimensions = (document.getOriginalWidth, document.getOriginalHeight) match {
        case (null, null) => None
        case (width, height) => Some(Dimensions(width, height))
      },
      ageRange = AgeRange(integerOption(document.getAgeRangeMin), integerOption(document.getAgeRangeMax)),
    )
  }

  private def integerOption(x: java.lang.Integer): Option[Int] = {
    x match {
      case null => None
      case _ => Some(x)
    }
  }

  private def convertAsset(document: VideoAssetDocument): VideoAsset = {
    VideoAsset(
      sizeKb = document.getSizeKb,
      bitrateKbps = document.getBitrateKbps,
      dimensions = Dimensions(document.getWidth, document.getHeight)
    )
  }
}
