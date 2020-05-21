package com.boclips.event.aggregator.domain.service.okr

import com.boclips.event.aggregator.domain.model.{Monthly, Search, VideoId}
import com.boclips.event.aggregator.domain.service.search.SearchResultPlayback
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createSearchRequest, createSearch, createSearchInteractions}
import org.apache.spark.rdd.RDD

class SearchMetricCalculatorTest extends IntegrationTest {

  "calculateMetrics" should "calculate percentage searches leading to playback" in sparkTest { implicit spark =>
    implicit val events: RDD[Search] = rdd(
      createSearch(
        request = createSearchRequest(userId = "aly", query = ""),
        interactions = createSearchInteractions(videosPlayed = List(SearchResultPlayback(videoId = VideoId("123"), videoIndex = Some(2), secondsPlayed = 2)))
      ),
      createSearch(
        request = createSearchRequest(userId = "aly", query = ""),
        interactions = createSearchInteractions(videosPlayed = List())
      ),
      createSearch(
        request = createSearchRequest(userId = "ben", query = ""),
        interactions = createSearchInteractions(videosPlayed = List(SearchResultPlayback(videoId = VideoId("124"), videoIndex = Some(3), secondsPlayed = 2)))
      ),
    )

    val metrics = SearchMetricCalculator calculateMetrics Monthly()

    metrics.head.percentageSearchesLeadingToPlayback shouldBe 2.0 / 3
  }

  it should "calculate percentage of searches with playbacks coming from top 3 results" in sparkTest { implicit spark =>
    implicit val events: RDD[Search] = rdd(
      createSearch(
        request = createSearchRequest(userId = "aly", query = ""),
        interactions = createSearchInteractions(videosPlayed = List(SearchResultPlayback(videoId = VideoId("123"), videoIndex = Some(2), secondsPlayed = 2)))
      ),
      createSearch(
        request = createSearchRequest(userId = "bob", query = ""),
        interactions = createSearchInteractions(videosPlayed = List(SearchResultPlayback(videoId = VideoId("124"), videoIndex = Some(3), secondsPlayed = 2)))
      ),
    )

    val metrics = SearchMetricCalculator calculateMetrics Monthly()

    metrics.head.percentagePlaybacksTop3 shouldBe 0.5
  }

  it should "exclude searches without playback from the top3 metric" in sparkTest { implicit spark =>
    implicit val events: RDD[Search] = rdd(
      createSearch(
        request = createSearchRequest(userId = "aly", query = ""),
        interactions = createSearchInteractions(videosPlayed = List(SearchResultPlayback(videoId = VideoId("123"), videoIndex = Some(2), secondsPlayed = 2)))
      ),
      createSearch(
        request = createSearchRequest(userId = "bob", query = ""),
        interactions = createSearchInteractions(videosPlayed = List())
      ),
    )

    val percentagesPlaybackTop3 = SearchMetricCalculator calculateMetrics Monthly()

    percentagesPlaybackTop3.head.percentagePlaybacksTop3 shouldBe 1
  }

}
