package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{Duration, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.{createVideoAssetDocument, createVideoDocument}

class DocumentToVideoConverterTest extends Test {

  it should "convert the id" in {
    val document = createVideoDocument(videoId = "the id")

    val video = DocumentToVideoConverter convert document

    video.id shouldBe VideoId("the id")
  }

  it should "convert the title" in {
    val video = DocumentToVideoConverter convert createVideoDocument(title = "the title")

    video.title shouldBe "the title"
  }

  it should "convert the content partner name" in {
    val video = DocumentToVideoConverter convert createVideoDocument(contentPartnerName = "the content partner")

    video.contentPartner shouldBe "the content partner"
  }

  it should "convert the playback provider type" in {
    val video = DocumentToVideoConverter convert createVideoDocument(playbackProvider = "YOUTUBE")

    video.playbackProvider shouldBe "YOUTUBE"
  }

  it should "convert the subjects" in {
    val video = DocumentToVideoConverter convert createVideoDocument(subjects = List("Maths"))

    video.subjects shouldBe List(Subject("Maths"))
  }

  it should "convert the age range" in {
    val video = DocumentToVideoConverter convert createVideoDocument(ageRange = AgeRange(Some(5), Some(15)))

    video.ageRange shouldBe AgeRange(Some(5), Some(15))
  }

  it should "be able to handle open age ranges" in {
    val video = DocumentToVideoConverter convert createVideoDocument(ageRange = AgeRange(None, None))

    video.ageRange shouldBe AgeRange(None, None)
  }

  it should "convert duration" in {
    val video = DocumentToVideoConverter convert createVideoDocument(durationSeconds = 100)

    video.duration shouldBe Duration.ofSeconds(100)
  }

  it should "convert content type when set" in {
    val video = DocumentToVideoConverter convert createVideoDocument(contentType = Some("NEWS"))

    video.contentType shouldBe Some("NEWS")
  }

  it should "convert content type when not set" in {
    val video = DocumentToVideoConverter convert createVideoDocument(contentType = None)

    video.contentType shouldBe None
  }

  it should "convert ingestion timestamp" in {
    val video = DocumentToVideoConverter convert createVideoDocument(ingestedAt = ZonedDateTime.parse("2020-02-01T12:13:14.15Z[UTC]"))

    video.ingestedAt shouldBe ZonedDateTime.parse("2020-02-01T12:13:14.15Z[UTC]")
  }

  it should "convert original dimensions" in {
    val video = DocumentToVideoConverter convert createVideoDocument(originalWidth = Some(1280), originalHeight = Some(720))

    video.originalDimensions should contain(Dimensions(1280, 720))
  }

  it should "handle missing original dimensions" in {
    val video = DocumentToVideoConverter convert createVideoDocument(originalWidth = None, originalHeight = None)

    video.originalDimensions shouldBe empty
  }

  it should "convert assets" in {
    val assetDocument = createVideoAssetDocument(id = "the-id", width = 480, height = 320, sizeKb = 1024, bitrateKbps = 100)
    val videoDocument = createVideoDocument(assets = Some(List(assetDocument)))
    val video = DocumentToVideoConverter convert videoDocument

    video.assets should have size 1
    video.assets should contain(VideoAsset(sizeKb = 1024, dimensions = Dimensions(480, 320), bitrateKbps = 100))
  }

  it should "handle missing assets" in {
    val video = DocumentToVideoConverter convert createVideoDocument(assets = None)

    video.assets shouldBe empty
  }

}
