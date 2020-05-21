package com.boclips.event.aggregator.testsupport.testfactories

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.presentation.formatters.schema.base.ExampleInstance
import org.bson.Document

import scala.collection.JavaConverters._

object ChannelFactory {
  def createChannel(
                     id: String = "channel-id",
                     name: String = "channel name",
                     details: ChannelDetails = ExampleInstance.create[ChannelDetails](),
                     ingest: ChannelIngest = ExampleInstance.create[ChannelIngest](),
                     pedagogy: ChannelPedagogy = ExampleInstance.create[ChannelPedagogy](),
                     marketing: ChannelMarketing = ExampleInstance.create[ChannelMarketing](),
                   ): Channel =
    Channel(
      id = ChannelId(id),
      name,
      details,
      ingest,
      pedagogy,
      marketing
    )

  def createChannelDocument(
                             id: String = "channel-id",
                             name: String = "channel name",
                             details: java.util.Map[String, Object] = createChannelDetailsDocument(),
                             ingest: java.util.Map[String, Object] = createChannelIngestDocument(),
                             pedagogy: java.util.Map[String, Object] = createChannelPedagogyDocument(),
                             marketing: java.util.Map[String, Object] = createChannelMarketingDocument()
                           ): Document =
    new Document(Map[String, Object](
      ("_id", id),
      ("name", name),
      ("details", details),
      ("ingest", ingest),
      ("pedagogy", pedagogy),
      ("marketing", marketing)
    ).asJava)

  implicit class OptionListStringAsJava(option: Option[List[String]]) {
    def asJava: java.util.List[String] = option match {
      case Some(list) => list.asJava
      case None => null
    }
  }

  implicit class OptionIntAsJava(option: Option[Int]) {
    def asJava: java.lang.Integer = option match {
      case Some(int) => int.asInstanceOf[java.lang.Integer]
      case None => null
    }
  }

  implicit class OptionBooleanAsJava(option: Option[Boolean]) {
    def asJava: java.lang.Boolean = option match {
      case Some(bool) => bool.asInstanceOf[java.lang.Boolean]
      case None => null
    }
  }

  def createChannelDetailsDocument(
                                    contentTypes: Option[List[String]] = Some(List("STOCK", "NEWS")),
                                    contentCategories: Option[List[String]] = Some(List("My category")),
                                    language: Option[String] = Some("en-US"),
                                    hubspotId: Option[String] = Some("hubspot-id"),
                                    contractId: Option[String] = Some("contract-id"),
                                    awards: Option[String] = Some("Awards text"),
                                    notes: Option[String] = Some("Notes text")
                                  ): java.util.Map[String, Object] =
    Map[String, Object](
      ("contentTypes", contentTypes.asJava),
      ("contentCategories", contentCategories.asJava),
      ("language", language.orNull),
      ("hubspotId", hubspotId.orNull),
      ("contractId", contractId.orNull),
      ("awards", awards.orNull),
      ("notes", notes.orNull),
    ).asJava

  def createChannelIngestDocument(
                                   _type: String = "MRSS",
                                   deliveryFrequency: Option[String] = None,
                                 ): java.util.Map[String, Object] =
    Map[String, Object](
      ("type", _type),
      ("deliveryFrequency", deliveryFrequency.orNull),
    ).asJava

  def createChannelPedagogyDocument(
                                     subjectNames: Option[List[String]] = Some(List("ART", "MATH")),
                                     ageRangeMin: Option[Int] = Some(6),
                                     ageRangeMax: Option[Int] = Some(16),
                                     bestForTags: Option[List[String]] = Some(List("cool", "tag")),
                                     curriculumAligned: Option[String] = Some("Curriculum text"),
                                     educationalResources: Option[String] = Some("Educational resources text"),
                                     transcriptProvided: Option[Boolean] = Some(true),
                                   ): java.util.Map[String, Object] =
    Map[String, Object](
      ("subjectNames", subjectNames.asJava),
      ("ageRangeMin", ageRangeMin.asJava),
      ("ageRangeMax", ageRangeMax.asJava),
      ("bestForTags", bestForTags.asJava),
      ("curriculumAligned", curriculumAligned.orNull),
      ("educationalResources", educationalResources.orNull),
      ("transcriptProvided", transcriptProvided.asJava),
    ).asJava

  def createChannelMarketingDocument(
                                      status: Option[String] = Some("Status"),
                                      oneLineIntro: Option[String] = Some("One line intro"),
                                      logos: Option[List[String]] = Some(
                                        List("http://logo1.com", "http://logo2.com")),
                                      showreel: Option[String] = Some("http://showreel.com"),
                                      sampleVideos: Option[List[String]] = Some(
                                        List("http://video1.com", "http://video2.com")),
                                    ): java.util.Map[String, Object] =
    Map[String, Object](
      ("status", status.orNull),
      ("oneLineIntro", oneLineIntro.orNull),
      ("logos", logos.asJava),
      ("showreel", showreel.orNull),
      ("sampleVideos", sampleVideos.asJava),
    ).asJava
}
