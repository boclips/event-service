package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{Duration, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._
import org.bson.Document

object DocumentToVideoConverter {

  def convert(document: Document): Video = {
    Video(
      id = VideoId(document.getString("_id")),
      title = document.getString("title"),
      contentPartner = document.getString("contentPartnerName"),
      playbackProvider = document.getString("playbackProviderType"),
      contentType = Option(document.getString("type")),
      subjects = document.getList[String]("subjects").map(name => Subject(name)),
      duration = Duration.ofSeconds(document.getInteger("durationSeconds").toLong),
      ingestedAt = ZonedDateTime.parse(document.getString("ingestedAt")),
      assets = document.getList[Document]("assets") match {
        case null => List()
        case assetDocuments => assetDocuments.map(convertAsset)
      },
      originalDimensions = (document.getInteger("originalWidth"), document.getInteger("originalHeight")) match {
        case (null, null) => None
        case (width, height) => Some(Dimensions(width, height))
      },
      ageRange = AgeRange(document.getIntOption("ageRangeMin"), document.getIntOption("ageRangeMax"))
    )
  }

  private def convertAsset(document: Document): VideoAsset = {
    VideoAsset(
      sizeKb = document.getInteger("sizeKb"),
      bitrateKbps = document.getInteger("bitrateKbps"),
      dimensions = Dimensions(document.getInteger("width"), document.getInteger("height"))
    )
  }
}
