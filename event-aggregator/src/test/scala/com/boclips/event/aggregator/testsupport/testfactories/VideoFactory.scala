package com.boclips.event.aggregator.testsupport.testfactories

import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.time.{Duration, LocalDate, ZoneOffset, ZonedDateTime}
import java.util

import com.boclips.event.aggregator.domain.model._
import org.bson.Document

import scala.collection.JavaConverters._

object VideoFactory {

  def createVideoAssetDocument(
                                id: String = "asset-id",
                                width: Int = 1920,
                                height: Int = 1080,
                                sizeKb: Int = 1000000,
                                bitrateKbps: Int = 60
                              ): Document = {
    new Document(Map[String, Object](
      ("id", id),
      ("width", width.asInstanceOf[Integer]),
      ("height", height.asInstanceOf[Integer]),
      ("sizeKb", sizeKb.asInstanceOf[Integer]),
      ("bitrateKbps", bitrateKbps.asInstanceOf[Integer])
    ).asJava)
  }

  def createVideoDocument(
                           videoId: String = "123",
                           title: String = "title",
                           contentPartnerName: String = "ContentPartner",
                           playbackProvider: String = "KALTURA",
                           contentType: Option[String] = None,
                           subjects: List[String] = List(),
                           ingestedAt: ZonedDateTime = ZonedDateTime.now(),
                           ageRange: AgeRange = AgeRange(None, None),
                           originalWidth: Option[Int] = None,
                           originalHeight: Option[Int] = None,
                           assets: Option[List[util.Map[String, Object]]] = None,
                           durationSeconds: Int = 180
                         ): Document = {
    new Document(Map[String, Object](
      ("_id", videoId),
      ("title", title),
      ("contentPartnerName", contentPartnerName),
      ("playbackProviderType", playbackProvider),
      ("subjects", subjects.asJava),
      ("ageRangeMin", ageRange.min.map(n => n.asInstanceOf[Integer]).orNull),
      ("ageRangeMax", ageRange.max.map(n => n.asInstanceOf[Integer]).orNull),
      ("type", contentType.orNull),
      ("ingestedAt", ingestedAt.format(ISO_DATE_TIME)),
      ("originalWidth", originalWidth.map(w => w.asInstanceOf[Integer]).orNull),
      ("originalHeight", originalHeight.map(h => h.asInstanceOf[Integer]).orNull),
      ("assets", assets.map(_.asJava).orNull),
      ("durationSeconds", durationSeconds.asInstanceOf[Integer])
    ).asJava)
  }

  def createVideo(
                   id: String = "123",
                   ingestedAt: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                   title: String = "title",
                   contentPartner: String = "content partner",
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
      contentPartner = contentPartner,
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
                           contentPartner: String = "AP",
                           periodStart: LocalDate = LocalDate.now(),
                           periodEnd: LocalDate = LocalDate.now(),
                           valueGbp: Double = 1
                         ): VideoStorageCharge = {
    VideoStorageCharge(
      videoId = videoId,
      contentPartner = contentPartner,
      periodStart = periodStart,
      periodEnd = periodEnd,
      valueGbp = valueGbp
    )
  }


  def createContentPartner
  (
    name: String = "the name",
    playbackProvider: String = "KALTURA",
  ): ContentPartner = {
    ContentPartner(
      name = name,
      playbackProvider = playbackProvider,
    )
  }
}
