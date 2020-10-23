package com.boclips.event.aggregator.infrastructure.mongo

import java.time.temporal.ChronoUnit
import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.events._
import com.boclips.event.aggregator.domain.model.search.Query
import com.boclips.event.aggregator.domain.model.users._
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.infrastructure.model.EventDocumentWithIdentity
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideosSearchEventDocument
import com.boclips.event.infrastructure.EventFields
import com.boclips.event.infrastructure.EventFields.URL
import org.bson.Document

class DocumentToEventConverterTest extends Test {

  implicit class DocumentExtensions(document: Document) {
    def asBoclipsUser(id: String = "boclips-id") = EventDocumentWithIdentity(document, BoclipsUserIdentity(UserId(id)))
  }

  "transforming SEARCH event" should "convert documents with type 'SEARCH'" in {
    val document = createVideosSearchEventDocument().asBoclipsUser()

    val event = DocumentToEventConverter.convert(document)

    event.isInstanceOf[VideosSearchedEvent] should be(true)
  }

  it should "convert timestamps" in {
    val date = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = createVideosSearchEventDocument(timestamp = date).asBoclipsUser()

    val event = DocumentToEventConverter convert document

    event.timestamp should be(date)
  }

  it should "convert userId" in {
    val document = createVideosSearchEventDocument(userId = "user").asBoclipsUser("user")

    val event = DocumentToEventConverter convert document

    event.userIdentity.id should contain(UserId("user"))
  }

  it should "convert query" in {
    val document = createVideosSearchEventDocument(query = "the query").asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.query should be(Query("the query"))
  }

  it should "convert page when exists" in {
    val document = createVideosSearchEventDocument(page = 2).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.pageIndex should be(2)
  }

  it should "default page to 0 when does not exist" in {
    val document = createVideosSearchEventDocument(page = null).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.pageIndex should be(0)
  }

  it should "covert video results when they exist" in {
    val document = createVideosSearchEventDocument(pageVideoIds = List("id1", "id2")).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.videoResults should be(Some(List(VideoId("id1"), VideoId("id2"))))
  }

  it should "covert video results when they do not exist" in {
    val document = createVideosSearchEventDocument(pageVideoIds = null).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.videoResults should be(None)
  }

  it should "convert total search results when they exist" in {
    val document = createVideosSearchEventDocument(totalResults = 8889).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideosSearchedEvent]

    event.totalResults should be(8889)
  }

  "transforming PLAYBACK event" should "convert date" in {
    val date = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createVideoSegmentPlayedEventDocument(timestamp = date).asBoclipsUser()

    val event = DocumentToEventConverter convert document

    event.timestamp should be(date)
  }

  it should "convert event id" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(id = "5e1278800000000000000000").asBoclipsUser()

    val event = DocumentToEventConverter convert document

    event.asInstanceOf[VideoSegmentPlayedEvent].id shouldBe "5e1278800000000000000000"
  }

  it should "convert user identity when boclips id specified and no external id" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(
      userId = Some("the user"),
      externalUserId = None,
    ).asBoclipsUser()

    val event = DocumentToEventConverter convert document

    event.userIdentity shouldBe BoclipsUserIdentity(UserId("the user"))
  }

  it should "convert user identity when both boclips id and external id specified" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(
      userId = Some("the user"),
      externalUserId = Some("pearson-user-1"),
    ).asBoclipsUser()

    val event = DocumentToEventConverter convert document

    event.userIdentity shouldBe ExternalUserIdentity(UserId("the user"), ExternalUserId("pearson-user-1"))
  }

//  it should "override userId when externalUserId is present and ExternalUserId Exists flag is true" in {
//    val document = EventFactory.createVideoSegmentPlayedEventDocument(
//      userId = Some("the user"),
//      externalUserId = Some("pearson-user-1"),
//    ).append(EventFields.EXTERNAL_USER_EXISTS,true)
//
//    val event = DocumentToEventConverter convert document
//
//    event.userIdentity shouldBe BoclipsUserIdentity(UserId("pearson-user-1"))
//  }

  it should "create anonymous user identity for events with device id" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(
      userId = None,
      externalUserId = None,
      deviceId = Some("device"),
    ).asBoclipsUser()

    val event = DocumentToEventConverter convert document

    event.userIdentity shouldBe AnonymousUserIdentity(Some(DeviceId("device")))
  }

  it should "create anonymous user identity for events without device id" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(
      userId = None,
      externalUserId = None,
      deviceId = None,
    ).asBoclipsUser()

    val event = DocumentToEventConverter convert document

    event.userIdentity shouldBe AnonymousUserIdentity(None)
  }

  it should "convert videoId" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(videoId = "the asset").asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.videoId should be(VideoId("the asset"))
  }

  it should "convert query when it is populated" in {
    val document = EventFactory
      .createVideoSegmentPlayedEventDocument(url = "https://example.com/abc/def?q=the%20query")
      .asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.url.get.path should be("/abc/def")
    event.query should be(Some(Query("the query")))
  }

  it should "convert query when it is not populated" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(url = "https://example.com").asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.query should be(None)
  }

  it should "convert referer id when present" in {
    val document = EventFactory
      .createVideoSegmentPlayedEventDocument(url = "https://teachers.boclips.com/videos/123?referer=refererId")
      .asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.refererId should be(Some(UserId("refererId")))
  }

  it should "set referer id to none when not present" in {
    val document = EventFactory
      .createVideoSegmentPlayedEventDocument(url = "https://example.com")
      .asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.refererId should be(None)
  }

  it should "convert videoIndex when exists" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(videoIndex = Some(6)).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.videoIndex should be(Some(6))
  }

  it should "convert videoIndex when does not exist" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(videoIndex = None).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.videoIndex should be(None)
  }

  it should "convert playback time" in {
    val document = EventFactory
      .createVideoSegmentPlayedEventDocument(segmentStartSeconds = 20, segmentEndSeconds = 30)
      .asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[VideoSegmentPlayedEvent]

    event.secondsWatched should be(10)
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
    ).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document)

    event.timestamp shouldBe ZonedDateTime.parse("2020-01-10T12:13:14Z")
    event.url should contain(Url.parse("http://teachers.boclips.com/apage"))
    event.userIdentity.id should contain(UserId("user"))
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
    ).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document)

    event.isInstanceOf[VideoInteractedWithEvent] shouldBe true
    event.asInstanceOf[VideoInteractedWithEvent].timestamp shouldBe date
    event.asInstanceOf[VideoInteractedWithEvent].userIdentity.id should contain(UserId("user1"))
    event.asInstanceOf[VideoInteractedWithEvent].query shouldBe Some(Query("antagonist"))
    event.asInstanceOf[VideoInteractedWithEvent].videoId shouldBe VideoId("666")
    event.asInstanceOf[VideoInteractedWithEvent].subtype shouldBe Some("HAD_FUN_WITH_VIDEO")
  }


  "convert other events" should "convert user id" in {
    val document = EventFactory
      .createArbitraryEventDocument("SOME_EVENT", userId = "user id")
      .asBoclipsUser()

    val event = DocumentToEventConverter.convert(document).asInstanceOf[OtherEvent]

    event.userIdentity.id should contain(UserId("user id"))
  }

  "transforming documents with type VIDEO_ADDED_TO_COLLECTION" should "convert obligatory properties" in {
    val date = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createVideoAddedToCollectionDocument(
      timestamp = date,
      userId = "user1",
      url = "https://teachers.boclips.com/videos?page=1&q=fractions",
      videoId = "videoId"
    ).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document)
    event.isInstanceOf[VideoAddedToCollectionEvent] shouldBe true
    event.asInstanceOf[VideoAddedToCollectionEvent].timestamp shouldBe date
    event.asInstanceOf[VideoAddedToCollectionEvent].userIdentity.id should contain(UserId("user1"))
    event.asInstanceOf[VideoAddedToCollectionEvent].query shouldBe Some(Query("fractions"))
    event.asInstanceOf[VideoAddedToCollectionEvent].videoId shouldBe VideoId("videoId")

  }

  "Transforming event documents of type PAGE_RENDERED" should "convert obligatory properties" in {
    val timestamp = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createPageRenderedDocument(
      timestamp = timestamp,
      userId = "renderer-user",
      url = "https://teachers.boclips.com/videos?page=1&q=fractions"
    ).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document)
    event.isInstanceOf[PageRenderedEvent] shouldBe true
    event.asInstanceOf[PageRenderedEvent].timestamp shouldBe timestamp
    event.asInstanceOf[PageRenderedEvent].userIdentity.id should contain(UserId("renderer-user"))
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
    ).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document)

    event.isInstanceOf[CollectionInteractedWithEvent] shouldBe true
    event.asInstanceOf[CollectionInteractedWithEvent].timestamp shouldBe timestamp
    event.asInstanceOf[CollectionInteractedWithEvent].userIdentity.id should contain(UserId("collection-user"))
    event.asInstanceOf[CollectionInteractedWithEvent].url shouldBe Some(Url.parse("https://teachers.boclips.com/videos?page=1&q=fractions"))
    event.asInstanceOf[CollectionInteractedWithEvent].collectionId shouldBe CollectionId("collection-id")
    event.asInstanceOf[CollectionInteractedWithEvent].query shouldBe Some(Query("fractions"))
    event.asInstanceOf[CollectionInteractedWithEvent].subtype shouldBe Some("NAVIGATE_TO_COLLECTION_DETAILS")
  }

  "Transforming event document of type PLATFORM_INTERACTED_WITH" should "convert properties when present" in {
    val timestamp = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createPlatformInteractedWithEventDocument(
      timestamp = timestamp,
      userId = "user-id",
      subtype = "EXIT",
      url = "https://teachers.boclips.com/videos?page=1&q=queries"
    ).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document)
    event.isInstanceOf[PlatformInteractedWithEvent] shouldBe true
    event.asInstanceOf[PlatformInteractedWithEvent].timestamp shouldBe timestamp
    event.asInstanceOf[PlatformInteractedWithEvent].userIdentity.id should contain(UserId("user-id"))
    event.asInstanceOf[PlatformInteractedWithEvent].url shouldBe Some(Url.parse("https://teachers.boclips.com/videos?page=1&q=queries"))
    event.asInstanceOf[PlatformInteractedWithEvent].subtype shouldBe Some("EXIT")
  }

  "Transforming event document of type PLATFORM_INTERACTED_WITH" should "convert properties when UserId not present" in {
    val timestamp = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    val document = EventFactory.createAnonymousPlatformInteractedWithEventDocument(
      timestamp = timestamp,
      subtype = "EXIT",
      url = "https://teachers.boclips.com/videos?page=1&q=queries"
    ).asBoclipsUser()

    val event = DocumentToEventConverter.convert(document)
    event.isInstanceOf[PlatformInteractedWithEvent] shouldBe true
    event.asInstanceOf[PlatformInteractedWithEvent].timestamp shouldBe timestamp
    event.asInstanceOf[PlatformInteractedWithEvent].userIdentity.id shouldBe None
    event.asInstanceOf[PlatformInteractedWithEvent].url shouldBe Some(Url.parse("https://teachers.boclips.com/videos?page=1&q=queries"))
    event.asInstanceOf[PlatformInteractedWithEvent].subtype shouldBe Some("EXIT")
  }
}
