package com.boclips.event.aggregator.infrastructure.mongo

import java.time.temporal.ChronoUnit
import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.events._
import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideosSearchEventDocument

class DocumentToEventConverterTest extends Test {

  "transforming SEARCH event" should "convert documents with type 'SEARCH'" in {
    val document = createVideosSearchEventDocument(eventType = "SEARCH")

    val event = DocumentToEventConverter.convert(document)

    event.isInstanceOf[VideosSearchedEvent] should be(true)
  }

  it should "convert timestamps" in {
    val date = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = createVideosSearchEventDocument(timestamp = date)

    val event = DocumentToEventConverter convert document

    event.timestamp should be(date)
  }

  it should "convert userId" in {
    val document = createVideosSearchEventDocument(userId = "user")

    val event = DocumentToEventConverter convert document

    event.userId should be(UserId("user"))
  }

  it should "convert query" in {
    val document = createVideosSearchEventDocument(query = "the query")

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.query should be(Query("the query"))
  }

  it should "convert page when exists" in {
    val document = createVideosSearchEventDocument(page = 2)

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.pageIndex should be(2)
  }

  it should "default page to 0 when does not exist" in {
    val document = createVideosSearchEventDocument(page = null)

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.pageIndex should be(0)
  }

  it should "covert video results when they exist" in {
    val document = createVideosSearchEventDocument(pageVideoIds = List("id1", "id2"))

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.videoResults should be(Some(List(VideoId("id1"), VideoId("id2"))))
  }

  it should "covert video results when they do not exist" in {
    val document = createVideosSearchEventDocument(pageVideoIds = null)

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.videoResults should be(None)
  }

  it should "convert total search results when they exist" in {
    val document = createVideosSearchEventDocument(totalResults = 8889)

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.totalResults should be(8889)
  }

  "transforming PLAYBACK event" should "convert date" in {
    val date = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createVideoSegmentPlayedEventDocument(timestamp = date)

    val event = DocumentToEventConverter convert document

    event.timestamp should be(date)
  }

  it should "convert event id" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(id = "5e1278800000000000000000")

    val event = DocumentToEventConverter convert document

    event.asInstanceOf[VideoSegmentPlayedEvent].id shouldBe "5e1278800000000000000000"
  }

  it should "convert playback device when exists" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(playbackDevice = Some("device"))

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.deviceId shouldBe Some(DeviceId("device"))
  }

  it should "skip playback device when does not exist" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(playbackDevice = None)

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.deviceId shouldBe None
  }

  it should "convert userId" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(userId = "the user")

    val event = DocumentToEventConverter convert document

    event.userId should be(UserId("the user"))
  }

  it should "convert assetId" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(assetId = "the asset", videoId = null)

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.videoId should be(VideoId("the asset"))
  }

  it should "convert videoId" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(videoId = "the asset", assetId = null)

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.videoId should be(VideoId("the asset"))
  }

  it should "convert query when it is populated" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(url = "https://example.com/abc/def?q=the%20query")

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.url.get.path should be("/abc/def")
    event.query should be(Some(Query("the query")))
  }

  it should "convert query when it is not populated" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(url = "https://example.com")

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.query should be(None)
  }

  it should "convert referer id when present" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(url = "https://teachers.boclips.com/videos/123?referer=refererId")

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.refererId should be(Some(UserId("refererId")))
  }

  it should "set referer id to none when not present" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(url = "https://example.com")

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.refererId should be(None)
  }

  it should "convert videoIndex when exists" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(videoIndex = Some(6))

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.videoIndex should be(Some(6))
  }

  it should "convert videoIndex when does not exist" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(videoIndex = None)

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.videoIndex should be(None)
  }

  it should "convert playback time" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(segmentStartSeconds = 20, segmentEndSeconds = 30)

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.secondsWatched should be(10)
  }

  it should "convert documents with type 'PLAYBACK'" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(eventType = "PLAYBACK")

    val event = DocumentToEventConverter.convert(document)

    event.isInstanceOf[VideoSegmentPlayedEvent] should be(true)
  }

  "transforming collection search events" should "convert documents with type RESOURCES_SEARCHED" in {
    val document = EventFactory.createCollectionsSearchedEventDocument(
      timestamp = ZonedDateTime.parse("2020-01-10T12:13:14Z"),
      userId = "user",
      url = "http://teachers.boclips.com/apage",
      query = "the query",
      pageIndex = 1,
      pageSize = 20,
      totalResults = 1000,
      collectionIds = List("collection"),
    )

    val event = DocumentToEventConverter.convert(document)

    event.timestamp shouldBe ZonedDateTime.parse("2020-01-10T12:13:14Z")
    event.url should contain(Url.parse("http://teachers.boclips.com/apage"))
    event.userId shouldBe UserId("user")
    event.asInstanceOf[CollectionSearchedEvent].pageIndex shouldBe 1
    event.asInstanceOf[CollectionSearchedEvent].pageSize shouldBe 20
    event.asInstanceOf[CollectionSearchedEvent].totalResults shouldBe 1000
    event.asInstanceOf[CollectionSearchedEvent].collectionResults should contain only CollectionId("collection")
  }

  "transforming video interacted with event" should "convert obligatory properties" in {
    val date = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createVideoInteractedWithEventDocument(timestamp = date,
      userId = "user1",
      url = "https://teachers.boclips.com/videos?page=1&q=antagonist",
      videoId = "666",
      subtype = "HAD_FUN_WITH_VIDEO"
    )

    val event = DocumentToEventConverter.convert(document)

    event.isInstanceOf[VideoInteractedWithEvent] shouldBe true
    event.asInstanceOf[VideoInteractedWithEvent].timestamp shouldBe date
    event.asInstanceOf[VideoInteractedWithEvent].userId shouldBe UserId("user1")
    event.asInstanceOf[VideoInteractedWithEvent].query shouldBe Some(Query("antagonist"))
    event.asInstanceOf[VideoInteractedWithEvent].videoId shouldBe VideoId("666")
    event.asInstanceOf[VideoInteractedWithEvent].subtype shouldBe Some("HAD_FUN_WITH_VIDEO")
  }


  "convert other events" should "convert user id" in {
    val document = EventFactory.createArbitraryEventDocument("SOME_EVENT", userId = "user id")

    val event = DocumentToEventConverter.convert(document).asInstanceOf[OtherEvent]

    event.userId should be(UserId("user id"))
  }

  "transforming documents with type VIDEO_ADDED_TO_COLLECTION" should "convert obligatory properties" in {
    val date = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createVideoAddedToCollectionDocument(
      timestamp = date,
      userId = "user1",
      url = "https://teachers.boclips.com/videos?page=1&q=fractions",
      videoId = "videoId"
    )

    val event = DocumentToEventConverter.convert(document)
    event.isInstanceOf[VideoAddedToCollectionEvent] shouldBe true
    event.asInstanceOf[VideoAddedToCollectionEvent].timestamp shouldBe date
    event.asInstanceOf[VideoAddedToCollectionEvent].userId shouldBe UserId("user1")
    event.asInstanceOf[VideoAddedToCollectionEvent].query shouldBe Some(Query("fractions"))
    event.asInstanceOf[VideoAddedToCollectionEvent].videoId shouldBe VideoId("videoId")

  }

  "Transforming event documents of type PAGE_RENDERED" should "convert obligatory properties" in {
    val timestamp = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createPageRenderedDocument(
      timestamp = timestamp,
      userId = "renderer-user",
      url = "https://teachers.boclips.com/videos?page=1&q=fractions"
    )

    val event = DocumentToEventConverter.convert(document)
    event.isInstanceOf[PageRenderedEvent] shouldBe true
    event.asInstanceOf[PageRenderedEvent].timestamp shouldBe timestamp
    event.asInstanceOf[PageRenderedEvent].userId shouldBe UserId("renderer-user")
    event.asInstanceOf[PageRenderedEvent].url shouldBe Some(Url.parse("https://teachers.boclips.com/videos?page=1&q=fractions"))
  }

  "Transforming event documents of type COLLECTION_INTERACTED_WITH" should "convert properties" in {
    val timestamp = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createCollectionInteractedWithEventDocument(
      timestamp = timestamp,
      userId = "collection-user",
      url = "https://teachers.boclips.com/videos?page=1&q=fractions",
      collectionId = "collection-id",
      subtype = "NAVIGATE_TO_COLLECTION_DETAILS"
    )

    val event = DocumentToEventConverter.convert(document)

    event.isInstanceOf[CollectionInteractedWithEvent] shouldBe true
    event.asInstanceOf[CollectionInteractedWithEvent].timestamp shouldBe timestamp
    event.asInstanceOf[CollectionInteractedWithEvent].userId shouldBe UserId("collection-user")
    event.asInstanceOf[CollectionInteractedWithEvent].url shouldBe Some(Url.parse("https://teachers.boclips.com/videos?page=1&q=fractions"))
    event.asInstanceOf[CollectionInteractedWithEvent].collectionId shouldBe CollectionId("collection-id")
    event.asInstanceOf[CollectionInteractedWithEvent].query shouldBe Some(Query("fractions"))
    event.asInstanceOf[CollectionInteractedWithEvent].subtype shouldBe Some("NAVIGATE_TO_COLLECTION_DETAILS")
  }


}
