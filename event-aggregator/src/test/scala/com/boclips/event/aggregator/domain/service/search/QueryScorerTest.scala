package com.boclips.event.aggregator.domain.service.search

import java.time.{Month, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.Monthly
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createSearch, createSearchRequest, createSearchInteractions}
import com.boclips.event.aggregator.testsupport.{IntegrationTest, TestTimestamps}

class QueryScorerTest extends IntegrationTest {

  it should "approximate scores using Beta distribution" in sparkTest { spark =>
    val scorer = new QueryScorer(priorHits = 1, priorMisses = 2)
    val searchWithPlayback = createSearch(
      request = createSearchRequest(userId = "u1", query = "HELLO"),
      interactions = createSearchInteractions(videosPlayed = List(SearchFactory.createSearchResultPlayback()))
    )
    val searchWithoutPlayback = createSearch(
      request = createSearchRequest(userId = "u2", query = "Hello"),
      interactions = createSearchInteractions(videosPlayed = List())
    )
    val searches = spark.sparkContext.parallelize(List(searchWithPlayback, searchWithoutPlayback))

    val scoredQueries = scorer.scoreQueries(searches, Monthly()).collect().toList

    scoredQueries.size shouldBe 1
    scoredQueries.head.query shouldBe "hello"
    scoredQueries.head.count shouldBe 2
    scoredQueries.head.hits shouldBe 1
    scoredQueries.head.score shouldBe 0.4
    scoredQueries.head.timePeriod shouldBe Monthly().dateRangeOf(ZonedDateTime.now())
  }

  it should "combine queries by the same user" in sparkTest { spark =>
    val scorer = new QueryScorer(priorHits = 1, priorMisses = 2)
    val searchWithPlayback = createSearch(
      request = createSearchRequest(userId = "u1", query = "HELLO"),
      interactions = createSearchInteractions(videosPlayed = List(SearchFactory.createSearchResultPlayback()))
    )
    val searchWithoutPlayback = createSearch(
      request = createSearchRequest(userId = "u1", query = "Hello"),
      interactions = createSearchInteractions(videosPlayed = List())
    )
    val searches = spark.sparkContext.parallelize(List(searchWithPlayback, searchWithoutPlayback))

    val scoredQueries = scorer.scoreQueries(searches, Monthly()).collect().toList

    scoredQueries.size shouldBe 1
    scoredQueries.head.query shouldBe "hello"
    scoredQueries.head.count shouldBe 1
    scoredQueries.head.hits shouldBe 1
    scoredQueries.head.score shouldBe 0.5
  }

  it should "calculate scores within a time period" in sparkTest { spark =>
    val scorer = new QueryScorer(priorHits = 1, priorMisses = 2)
    val aprilSearch = createSearch(
      request = createSearchRequest(userId = "u1", query = "HELLO", timestamp = TestTimestamps.thisYearIn(Month.APRIL)),
      interactions = createSearchInteractions(videosPlayed = List())
    )
    val maySearch = createSearch(
      request = createSearchRequest(userId = "u1", query = "HELLO", timestamp = TestTimestamps.thisYearIn(Month.MAY)),
      interactions = createSearchInteractions(videosPlayed = List())
    )
    val searches = spark.sparkContext.parallelize(List(aprilSearch, maySearch))

    val scoredQueries = scorer.scoreQueries(searches, Monthly()).collect().toList

    scoredQueries.size shouldBe 2
    scoredQueries.head.query shouldBe "hello"
    scoredQueries.head.count shouldBe 1
    scoredQueries.head.hits shouldBe 0
    scoredQueries.head.score shouldBe 0.25
  }

}
