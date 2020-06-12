package com.boclips.event.aggregator.domain.service.search

import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory._
import com.boclips.event.aggregator.testsupport.testfactories.SessionFactory.createSession
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity

//noinspection RedundantDefaultArgument
class SessionSearchAssemblerTest extends Test {

  "aggregateSearchEvents" should "create one high level event for search events with the same query" in {
    val timestamp = ZonedDateTime.now(UTC)
    val events = createSession(events = List(
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("user id"), query = "the query", timestamp = timestamp.plusMinutes(1)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("user id"), query = "the query", timestamp = timestamp)
    ))

    val highLevelEvents = new SessionSearchAssembler() assembleSearchesInSession events

    highLevelEvents should have size 1
    highLevelEvents.head.request.userId shouldBe UserId("user id")
    highLevelEvents.head.request.timestamp shouldBe timestamp
    highLevelEvents.head.request.query shouldBe Query("the query")
  }

  it should "include number of pages seen" in {
    val events = createSession(events = List(
      createVideosSearchedEvent(pageIndex = 0),
      createVideosSearchedEvent(pageIndex = 1),
      createVideosSearchedEvent(pageIndex = 0)
    ))

    val highLevelEvents = new SessionSearchAssembler() assembleSearchesInSession events

    highLevelEvents should have size 1
    highLevelEvents.head.interactions.resultPagesSeen shouldBe 2
  }

  it should "include the total number of results retrieved" in {
    val events = createSession(events = List(
      createVideosSearchedEvent(totalResults = 700),
      createVideosSearchedEvent(totalResults = 25),
    ))

    val highLevelEvents = new SessionSearchAssembler() assembleSearchesInSession events

    highLevelEvents should have size 1
    highLevelEvents.head.response.totalResults shouldBe 700
  }

  it should "include the minimum number of results retrieved" in {
    val events = createSession(events = List(
      createVideosSearchedEvent(totalResults = 700),
      createVideosSearchedEvent(totalResults = 25),
    ))

    val highLevelEvents = new SessionSearchAssembler() assembleSearchesInSession events

    highLevelEvents should have size 1
    highLevelEvents.head.response.minResults shouldBe 25
  }


  it should "include played videos information" in {
    val events = createSession(events = List(
      createVideosSearchedEvent(query = "q", pageIndex = 0),
      createVideoSegmentPlayedEvent(query = Some("q"), videoId = "v1", videoIndex = Some(1), secondsWatched = 20),
      createVideoSegmentPlayedEvent(query = Some("q"), videoId = "v1", videoIndex = Some(1), secondsWatched = 10)
    ))

    val highLevelEvents = new SessionSearchAssembler() assembleSearchesInSession events

    highLevelEvents should have size 1
    highLevelEvents.head.interactions.videosPlayed should have size 1
    highLevelEvents.head.interactions.videosPlayed.head.videoId shouldBe VideoId("v1")
    highLevelEvents.head.interactions.videosPlayed.head.videoIndex shouldBe Some(1)
    highLevelEvents.head.interactions.videosPlayed.head.secondsPlayed shouldBe 30
  }

  it should "mark impressions with playback as interacted with" in {
    val session = createSession(events = List(
      createVideosSearchedEvent(query = "q", videoResults = Some(List("v1"))),
      createVideoSegmentPlayedEvent(query = Some("q"), videoId = "v1"),
    ))

    val searches = new SessionSearchAssembler() assembleSearchesInSession session

    searches should have size 1
    searches.head.response.videoResults should have size 1
    searches.head.response.videoResults.head.videoId shouldBe VideoId("v1")
    searches.head.response.videoResults.head.interaction shouldBe true
  }

  it should "mark impressions as not interacted with when there is no playback" in {
    val session = createSession(events = List(
      createVideosSearchedEvent(query = "q", videoResults = Some(List("v1"))),
      createVideoSegmentPlayedEvent(query = Some("q"), videoId = "v2"),
    ))

    val searches = new SessionSearchAssembler() assembleSearchesInSession session

    searches should have size 1
    searches.head.response.videoResults should have size 1
    searches.head.response.videoResults.head.videoId shouldBe VideoId("v1")
    searches.head.response.videoResults.head.interaction shouldBe false
  }

  it should "mark impressions as interacted with when there is an interaction event" in {
    val session = createSession(events = List(
      createVideosSearchedEvent(query = "q", videoResults = Some(List("v1"))),
      createVideoInteractedWithEvent(query = Some("q"), videoId = "v1"),
    ))

    val searches = new SessionSearchAssembler() assembleSearchesInSession session

    searches should have size 1
    searches.head.response.videoResults should have size 1
    searches.head.response.videoResults.head.videoId shouldBe VideoId("v1")
    searches.head.response.videoResults.head.interaction shouldBe true
  }

  it should "mark impressions as interacted with when video is added to collection" in {
    val session = createSession(events = List(
      createVideosSearchedEvent(query = "earth day", videoResults = Some(List("v1"))),
      createVideoAddedToCollectionEvent(query = Some("earth day"), videoId = "v1")
    ))
    val searches = new SessionSearchAssembler() assembleSearchesInSession session
    searches should have size 1
    searches.head.response.videoResults should have size 1
    searches.head.response.videoResults.head.videoId shouldBe VideoId("v1")
    searches.head.response.videoResults.head.interaction shouldBe true

  }

  it should "combine video result ids from individual searches" in {
    val events = createSession(events = List(
      createVideosSearchedEvent(videoResults = Some(List("v1", "v2"))),
      createVideosSearchedEvent(videoResults = Some(List("v1", "v2"))),
      createVideosSearchedEvent(videoResults = Some(List("v3", "v4")))
    ))

    val highLevelEvents = new SessionSearchAssembler() assembleSearchesInSession events

    highLevelEvents should have size 1
    highLevelEvents.head.response.videoResults.map(_.videoId) shouldBe Set(VideoId("v1"), VideoId("v2"), VideoId("v3"), VideoId("v4"))
  }

  it should "include search query params" in {
    val events = createSession(events = List(
      createVideosSearchedEvent(url = (Url.parse("https://teachers.boclips.com/videos?age_range=3-5&duration=0-120&page=1&q=my%20query"))),
      createVideosSearchedEvent(url = (Url.parse("https://teachers.boclips.com/videos?q=my%20query"))),
      createVideosSearchedEvent(url = (Url.parse("https://teachers.boclips.com/videos?q=my%20query&duration=5&test=5"))),
    ))

    val searches = new SessionSearchAssembler() assembleSearchesInSession events

    searches should have size 1
    searches.head.request.urlParamsKeys shouldBe Set("age_range", "duration", "q", "page", "test")
  }

  it should "include collection impressions" in {
    val events = createSession(events = List(
      createVideosSearchedEvent(query = "a query"),
      createCollectionsSearchedEvent(query = "a query", collectionResults = List("collection-id"))
    ))

    val searches = new SessionSearchAssembler() assembleSearchesInSession events

    searches should have size 1
    searches.head.response.collectionResults should have size 1
    searches.head.response.collectionResults.head.collectionId shouldBe CollectionId("collection-id")
  }

  it should "mark collection impression as interacted with when there is an interaction" in {
    val events = createSession(events = List(
      createVideosSearchedEvent(query = "a query"),
      createCollectionsSearchedEvent(query = "a query", collectionResults = List("collection-id-1", "collection-id-2")),
      createCollectionInteractedWithEvent(collectionId = "collection-id-1", query = Some("a query"))
    ))

    val searches = new SessionSearchAssembler() assembleSearchesInSession events

    searches should have size 1
    searches.head.response.collectionResults should have size 2

    val collection1 :: collection2 :: _ = searches.head.response.collectionResults.toList.sortBy(_.collectionId.value)

    collection1.interaction shouldBe true
    collection2.interaction shouldBe false
  }

  it should "include the URL Host" in {
    val events = createSession(events = List(createVideosSearchedEvent(query = "sharkira", url = Url.parse("https://teachers.boclips.com/videos?page=1&q=sharkira"))))
    val searches = new SessionSearchAssembler() assembleSearchesInSession events
    searches.head.request.url.get.host.toString shouldBe "teachers.boclips.com"
  }

  it should "include the URL Path" in {
    val events = createSession(events = List(createVideosSearchedEvent(query = "sharkira", url = Url.parse("https://teachers.boclips.com/videos?page=1&q=sharkira"))))
    val searches = new SessionSearchAssembler() assembleSearchesInSession events
    searches.head.request.url.get.path.toString shouldBe "/videos"
  }
}
