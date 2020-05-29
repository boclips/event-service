package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.Date

import com.boclips.event.aggregator.domain.model.{AgeRange, CollectionId, UserId, VideoId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.collection.CollectionDocument

import scala.collection.JavaConverters._

class DocumentToCollectionConverterTest extends Test {

  it should "convert id" in {
    val document = CollectionDocument.sample.id("collection-id").build()

    val collection = DocumentToCollectionConverter convert document

    collection.id shouldBe CollectionId("collection-id")
  }

  it should "convert title" in {
    val document = CollectionDocument.sample.title("collection title").build()

    val collection = DocumentToCollectionConverter convert document

    collection.title shouldBe "collection title"
  }

  it should "convert description" in {
    val document = CollectionDocument.sample.description("collection description").build()

    val collection = DocumentToCollectionConverter convert document

    collection.description shouldBe "collection description"
  }

  it should "convert subjects" in {
    val document = CollectionDocument.sample.subjects(List("maths").asJava).build()

    val collection = DocumentToCollectionConverter convert document

    collection.subjects shouldBe List("maths")
  }

  it should "convert age ranges" in {
    val document = CollectionDocument.sample.minAge(7).maxAge(11).build()

    val collection = DocumentToCollectionConverter convert document

    collection.ageRange shouldBe AgeRange(Some(7), Some(11))
  }

  it should "handle open age ranges" in {
    val document = CollectionDocument.sample.minAge(null).maxAge(null).build()

    val collection = DocumentToCollectionConverter convert document

    collection.ageRange shouldBe AgeRange(None, None)
  }

  it should "convert video ids" in {
    val document = CollectionDocument.sample.videoIds(List("video-1").asJava).build()

    val collection = DocumentToCollectionConverter convert document

    collection.videoIds shouldBe List(VideoId("video-1"))
  }

  it should "convert owner id" in {
    val document = CollectionDocument.sample.ownerId("owner-id-1").build()

    val collection = DocumentToCollectionConverter convert document

    collection.ownerId shouldBe UserId("owner-id-1")
  }

  it should "convert bookmarks" in {
    val document = CollectionDocument.sample.bookmarks(List("user-id-2").asJava).build()

    val collection = DocumentToCollectionConverter convert document

    collection.bookmarks shouldBe List(UserId("user-id-2"))
  }

  it should "convert creation date" in {
    val document = CollectionDocument.sample.createdTime(
      Date.from(ZonedDateTime.of(2019, 1, 2, 3, 4, 5, 300, ZoneOffset.UTC).toInstant)
    ).build()

    val collection = DocumentToCollectionConverter convert document

    collection.createdTime shouldBe ZonedDateTime.of(2019, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC)
  }

  it should "convert last update date" in {
    val document = CollectionDocument.sample.createdTime(
      Date.from(ZonedDateTime.of(2020, 1, 2, 3, 4, 5, 300, ZoneOffset.UTC).toInstant)
    ).build()

    val collection = DocumentToCollectionConverter convert document

    collection.createdTime shouldBe ZonedDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC)
  }

  it should "convert deletion flag" in {
    (DocumentToCollectionConverter
      convert
      CollectionDocument.sample.deleted(true).build()
      ).deleted shouldBe true
    (DocumentToCollectionConverter
      convert
      CollectionDocument.sample.deleted(false).build()
      ).deleted shouldBe false
  }

  it should "convert public flag" in {
    (DocumentToCollectionConverter
      convert
      CollectionDocument.sample.discoverable(true).build()
      ).public shouldBe true
    (DocumentToCollectionConverter
      convert
      CollectionDocument.sample.discoverable(false).build()
      ).public shouldBe false
  }
}
