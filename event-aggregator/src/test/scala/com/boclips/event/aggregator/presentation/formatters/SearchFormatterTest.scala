package com.boclips.event.aggregator.presentation.formatters

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.search.{CollectionImpression, SearchImpression}
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.domain.model.{search, _}
import com.boclips.event.aggregator.domain.service.search.SearchResultPlayback
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createSearch, createSearchInteractions, createSearchRequest, createSearchResponse}
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity

class SearchFormatterTest extends Test {

  it should "write user id" in {
    val json = SearchFormatter formatRow createSearch(request = createSearchRequest(userIdentity = createBoclipsUserIdentity("user-1")))

    json.get("userId").getAsString shouldBe "user-1"
  }

  it should "write query" in {
    val json = SearchFormatter formatRow createSearch(request = createSearchRequest(query = "the query"))

    json.get("query").getAsString shouldBe "the query"
  }

  it should "write timestamp" in {
    val json = SearchFormatter formatRow createSearch(request = createSearchRequest(timestamp = ZonedDateTime.of(2019, 4, 23, 18, 25, 43, 511000000, ZoneOffset.UTC)))

    json.get("timestamp").getAsString shouldBe "2019-04-23T18:25:43.511Z"
  }

  it should "write number of result pages seen" in {
    val json = SearchFormatter formatRow createSearch(interactions = createSearchInteractions(resultPagesSeen = 5))

    json.get("resultPagesSeen").getAsInt shouldBe 5
  }

  it should "write the total number of results" in {
    val json = SearchFormatter formatRow createSearch(response = createSearchResponse(totalResults = 789))
    json.get("totalResults").getAsInt shouldBe 789
  }

  it should "write number of videos played in search results" in {
    val json = SearchFormatter formatRow createSearch(interactions = createSearchInteractions(videosPlayed = List(
      SearchResultPlayback(videoId = VideoId("1"), videoIndex = Some(2), secondsPlayed = 10),
      SearchResultPlayback(videoId = VideoId("2"), videoIndex = Some(5), secondsPlayed = 20)
    )))

    json.get("videosPlayed").getAsInt shouldBe 2
    json.get("videoSecondsPlayed").getAsInt shouldBe 30
  }

  it should "write an interaction number" in {
    val json = SearchFormatter formatRow createSearch(response = createSearchResponse(
      videoResults = Set(
        SearchImpression(videoId = VideoId("1"), interaction = false),
        SearchImpression(videoId = VideoId("2"), interaction = true),
        SearchImpression(videoId = VideoId("3"), interaction = true),
      ),
      collectionResults = Set(
        CollectionImpression(collectionId = CollectionId("collection-1"), interaction = true),
        search.CollectionImpression(collectionId = CollectionId("collection-2"), interaction = false),
      )
    ))

    json.getInt("interactionCount") shouldBe 3
    json.getInt("videoInteractionCount") shouldBe 2
    json.getInt("collectionInteractionCount") shouldBe 1
  }

  it should "write the search id" in {
    val json = SearchFormatter formatRow createSearch(request = createSearchRequest(id = "the id"))

    json.getString("id") shouldBe "the id"
  }

  it should "write the number of collection results" in {

    val json = SearchFormatter formatRow createSearch(response = createSearchResponse(collectionResults = Set(
      search.CollectionImpression(collectionId = CollectionId("collection-1"), interaction = true),
      search.CollectionImpression(collectionId = CollectionId("collection-2"), interaction = false),
    )))

    json.getInt("collectionResultsCount") shouldBe 2
  }


  it should "write the minimum number of search resutls " in {

    val json = SearchFormatter formatRow createSearch(response = createSearchResponse(minResults = 28))

    json.getInt("minResults") shouldBe 28
  }

  it should "write the URL host and path " in {

    val json = SearchFormatter formatRow createSearch(request = createSearchRequest(query = "aa", url = Some(Url.parse("http://example.com/a"))))
    json.getString("urlHost") shouldBe "example.com"
    json.getString("urlPath") shouldBe "/a"
  }
  it should "url param keys" in {

    val json = SearchFormatter formatRow createSearch(request = createSearchRequest(urlParamsKeys = Set("age_range", "page", "q")))
    json.getStringList("urlParamKeys") shouldBe List("age_range", "page", "q")
  }

}
