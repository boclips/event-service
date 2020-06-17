package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.domain.model.search.SearchPhrase
import com.boclips.event.aggregator.testsupport.Test

class SearchPhraseTest extends Test {

  "count" should "return total number of searches" in {
    val searchPhrase = SearchPhrase("chemistry", 10, 20)

    searchPhrase.count shouldBe 30
  }

  "estimatePlaybackProbability" should "return probability of view from search" in {
    val searchPhrase = SearchPhrase("chemistry", 10, 20)

    searchPhrase.estimatePlaybackProbability(1, 2) shouldBe 1.0 / 3
  }
}
