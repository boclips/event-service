package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{AgeRange, CollectionId, UserId, VideoId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.CollectionFactory

class DocumentToCollectionConverterTest extends Test {

  it should "convert id" in {
    val document = CollectionFactory.createCollectionDocument(id = "collection-id")

    val collection = DocumentToCollectionConverter convert document

    collection.id shouldBe CollectionId("collection-id")
  }

  it should "convert title" in {
    val document = CollectionFactory.createCollectionDocument(title = "collection title")

    val collection = DocumentToCollectionConverter convert document

    collection.title shouldBe "collection title"
  }

  it should "convert description" in {
    val document = CollectionFactory.createCollectionDocument(description = "collection description")

    val collection = DocumentToCollectionConverter convert document

    collection.description shouldBe "collection description"
  }

  it should "convert subjects" in {
    val document = CollectionFactory.createCollectionDocument(subjects = List("maths"))

    val collection = DocumentToCollectionConverter convert document

    collection.subjects shouldBe List("maths")
  }

  it should "convert age ranges" in {
    val document = CollectionFactory.createCollectionDocument(ageRange = AgeRange(Some(7), Some(11)))

    val collection = DocumentToCollectionConverter convert document

    collection.ageRange shouldBe AgeRange(Some(7), Some(11))
  }

  it should "handle open age ranges" in {
    val document = CollectionFactory.createCollectionDocument(ageRange = AgeRange(None, None))

    val collection = DocumentToCollectionConverter convert document

    collection.ageRange shouldBe AgeRange(None, None)
  }

  it should "convert video ids" in {
    val document = CollectionFactory.createCollectionDocument(videoIds = List("video-1"))

    val collection = DocumentToCollectionConverter convert document

    collection.videoIds shouldBe List(VideoId("video-1"))
  }

  it should "convert owner id" in {
    val document = CollectionFactory.createCollectionDocument(ownerId = "owner-id-1")

    val collection = DocumentToCollectionConverter convert document

    collection.ownerId shouldBe UserId("owner-id-1")
  }

  it should "convert bookmarks" in {
    val document = CollectionFactory.createCollectionDocument(bookmarks = List("user-id-2"))

    val collection = DocumentToCollectionConverter convert document

    collection.bookmarks shouldBe List(UserId("user-id-2"))
  }

  it should "convert creation date" in {
    val document = CollectionFactory.createCollectionDocument(createdTime = ZonedDateTime.of(2019, 1, 2, 3, 4, 5, 300, ZoneOffset.UTC))

    val collection = DocumentToCollectionConverter convert document

    collection.createdTime shouldBe ZonedDateTime.of(2019, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC)
  }

  it should "convert last update date" in {
    val document = CollectionFactory.createCollectionDocument(createdTime = ZonedDateTime.of(2020, 1, 2, 3, 4, 5, 300, ZoneOffset.UTC))

    val collection = DocumentToCollectionConverter convert document

    collection.createdTime shouldBe ZonedDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC)
  }

  it should "convert deletion flag" in {
    (DocumentToCollectionConverter convert CollectionFactory.createCollectionDocument(deleted = true)).deleted shouldBe true
    (DocumentToCollectionConverter convert CollectionFactory.createCollectionDocument(deleted = false)).deleted shouldBe false
  }

  it should "convert public flag" in {
    (DocumentToCollectionConverter convert CollectionFactory.createCollectionDocument(public = true)).public shouldBe true
    (DocumentToCollectionConverter convert CollectionFactory.createCollectionDocument(public = false)).public shouldBe false
  }

}
