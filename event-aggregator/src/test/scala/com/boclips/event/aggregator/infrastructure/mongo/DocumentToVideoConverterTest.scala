package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.videos.{Dimensions, VideoAsset, VideoId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.channel.CategoryWithAncestorsDocument
import com.boclips.event.infrastructure.video.{VideoAssetDocument, VideoDocument, VideoTopicDocument}

import java.time.{Duration, ZonedDateTime}
import java.util.Collections.{emptySet, singleton, singletonList}
import java.util.Locale
import scala.collection.JavaConverters._

class DocumentToVideoConverterTest extends Test {

  it should "convert the id" in {
    val document = VideoDocument.sample().id("the id").build()

    val video = DocumentToVideoConverter convert document

    video.id shouldBe VideoId("the id")
  }

  it should "convert the title" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().title("the title").build()

    video.title shouldBe "the title"
  }

  it should "convert channel id" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().channelId("channel-id").build()

    video.channelId.value shouldBe "channel-id"
  }

  it should "convert the playback provider type" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().playbackProviderType("YOUTUBE").build()

    video.playbackProvider shouldBe "YOUTUBE"
  }

  it should "convert the playback ID" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().playbackId("cool playback id").build()

    video.playbackId shouldBe "cool playback id"
  }

  it should "convert the subjects" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().subjects(singleton("Maths")).build()

    video.subjects shouldBe List(Subject("Maths"))
  }

  it should "convert the age range" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().ageRangeMin(5).ageRangeMax(15).build()

    video.ageRange shouldBe AgeRange(Some(5), Some(15))
  }

  it should "be able to handle open age ranges" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().ageRangeMin(null).ageRangeMax(null).build()

    video.ageRange shouldBe AgeRange(None, None)
  }

  it should "convert duration" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().durationSeconds(100).build()

    video.duration shouldBe Duration.ofSeconds(100)
  }

  it should "convert content type when set" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().`type`("NEWS").build()

    video.contentType shouldBe Some("NEWS")
  }

  it should "convert content type when not set" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().`type`(null).build()

    video.contentType shouldBe None
  }

  it should "convert ingestion timestamp" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().ingestedAt("2020-02-01T12:13:14.15Z[UTC]").build()

    video.ingestedAt shouldBe ZonedDateTime.parse("2020-02-01T12:13:14.15Z[UTC]")
  }

  it should "convert original dimensions" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().originalWidth(1280).originalHeight(720).build()

    video.originalDimensions should contain(Dimensions(1280, 720))
  }

  it should "handle missing original dimensions" in {
    val video = DocumentToVideoConverter convert VideoDocument.sample().originalWidth(null).originalHeight(null).build()

    video.originalDimensions shouldBe empty
  }

  it should "convert assets" in {
    val assetDocument = VideoAssetDocument.sample().id("the-id").width(480).height(320).sizeKb(1024).bitrateKbps(100).build()
    val videoDocument = VideoDocument.sample().assets(singletonList(assetDocument)).build()
    val video = DocumentToVideoConverter convert videoDocument

    video.assets should have size 1
    video.assets should contain(VideoAsset(sizeKb = 1024, dimensions = Dimensions(480, 320), bitrateKbps = 100))
  }

  it should "handle missing assets" in {
    val video = DocumentToVideoConverter.convert(
      VideoDocument.sample().assets(null).build
    )

    video.assets shouldBe empty
  }

  it should "convert promoted flag" in {
    val video = DocumentToVideoConverter.convert(
      VideoDocument.sample.promoted(true).build
    )

    video.promoted shouldBe true
  }

  it should "convert deactivated flag" in {
    val video = DocumentToVideoConverter.convert(
      VideoDocument.sample.deactivated(true).build
    )

    video.deactivated shouldBe true
  }

  it should "convert a null deactivated value to false" in {
    val video = DocumentToVideoConverter.convert(
      VideoDocument.sample.deactivated(null).build
    )

    video.deactivated shouldBe false
  }

  it should "convert topics" in {
    val video = DocumentToVideoConverter
      .convert(
        VideoDocument
          .sample
          .topics(
            List(
              VideoTopicDocument.builder()
                .name("topic")
                .confidence(0.5)
                .language("fr")
                .parent(
                  VideoTopicDocument.builder()
                    .name("parent-topic")
                    .confidence(0.8)
                    .language("it")
                    .build
                )
                .build
            ).asJava
          )
          .build
      )

    val firstTopic = video.topics.head
    firstTopic.name shouldBe "topic"
    firstTopic.confidence shouldBe 0.5
    firstTopic.language shouldBe Locale.FRENCH
    val parentTopic = firstTopic.parent.orNull
    parentTopic.name shouldBe "parent-topic"
    parentTopic.confidence shouldBe 0.8
    parentTopic.language shouldBe Locale.ITALIAN
    parentTopic.parent shouldBe None
  }

  it should "convert keywords" in {
    val video = DocumentToVideoConverter
      .convert(
        VideoDocument
          .sample
          .keywords(
            List(
              "some",
              "key",
              "words"
            ).asJava
          )
          .build
      )

    video.keywords should be (List("some", "key", "words"))
  }

  it should "convert source video reference" in {
    val video = DocumentToVideoConverter
      .convert(
        VideoDocument
          .sample
          .sourceVideoReference("vr")
          .build
      )
    video.sourceVideoReference should be (Some("vr"))
  }
}
