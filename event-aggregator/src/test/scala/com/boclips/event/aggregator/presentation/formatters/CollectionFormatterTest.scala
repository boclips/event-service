package com.boclips.event.aggregator.presentation.formatters

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{AgeRange, CollectionWithRelatedData}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.CollectionFactory.createCollection
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createCollectionInteractedWithEvent

class CollectionFormatterTest extends Test {

  it should "write collection id" in {
    val collection = createCollection(id = "col-id")


    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.get("id").getAsString shouldBe "col-id"
  }

  it should "write collection title" in {
    val collection = createCollection(title = "the title")

    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.get("title").getAsString shouldBe "the title"
  }

  it should "write collection description" in {
    val collection = createCollection(description = "the description")

    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.get("description").getAsString shouldBe "the description"
  }

  it should "write collection subjects" in {
    val collection = createCollection(subjects = List("maths"))

    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.getStringList("subjects") shouldBe List("maths")
  }

  it should "write collection age range" in {
    val collection = createCollection(ageRange = AgeRange(Some(8), Some(9)))

    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.getStringList("ages") shouldBe List("08", "09")
  }

  it should "write video ids" in {
    val collection = createCollection(videoIds = List("v1", "v2"))

    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.getStringList("videoIds") shouldBe List("v1", "v2")
  }

  it should "write owner id" in {
    val collection = createCollection(ownerId = "the owner")

    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.getString("ownerId") shouldBe "the owner"
  }

  it should "write bookmarks" in {
    val collection = createCollection(bookmarks = List("user-1", "user-2"))

    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.getStringList("bookmarks") shouldBe List("user-1", "user-2")
  }

  it should "write creation timestamp" in {
    val collection = createCollection(createdTime = ZonedDateTime.of(2019, 5, 4, 2, 2, 6, 0, ZoneOffset.UTC))

    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.getString("createdAt") shouldBe "2019-05-04T02:02:06Z"
  }

  it should "write last update timestamp" in {
    val collection = createCollection(updatedTime = ZonedDateTime.of(2019, 5, 4, 2, 2, 6, 0, ZoneOffset.UTC))

    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List())

    json.getString("updatedAt") shouldBe "2019-05-04T02:02:06Z"
  }

  it should "write deletion flag" in {
    CollectionFormatter.formatRow(CollectionWithRelatedData(createCollection(deleted = true), List(), List())).getBool("deleted") shouldBe true
    CollectionFormatter.formatRow(CollectionWithRelatedData(createCollection(deleted = false), List(), List())).getBool("deleted") shouldBe false
  }

  it should "write public flag" in {
    CollectionFormatter.formatRow(CollectionWithRelatedData(createCollection(public = true), List(), List())).getBool("public") shouldBe true
    CollectionFormatter.formatRow(CollectionWithRelatedData(createCollection(public = false), List(), List())).getBool("public") shouldBe false
  }

  it should "write collection interactions" in {
    val collection = createCollection()
    val interaction = createCollectionInteractedWithEvent()
    val json = CollectionFormatter formatRow CollectionWithRelatedData(collection, List(), List(interaction))

    json.getAsJsonArray("interactions").size shouldBe 1

  }
}
