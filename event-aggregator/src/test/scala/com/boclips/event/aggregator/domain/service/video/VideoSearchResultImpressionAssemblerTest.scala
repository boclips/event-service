package com.boclips.event.aggregator.domain.service.video

import com.boclips.event.aggregator.domain.model.{SearchImpression, VideoId}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createSearch, createSearchResponse}

class VideoSearchResultImpressionAssemblerTest extends IntegrationTest {

  it should "create one impression for each video" in sparkTest { implicit spark =>
    val search = createSearch(response = createSearchResponse(videoResults = Set(
      SearchImpression(videoId = VideoId("v1"), interaction = false),
      SearchImpression(videoId = VideoId("v2"), interaction = true),
    )))
    val searches = rdd(search)

    val impressions = VideoSearchResultImpressionAssembler(searches).collect().toList

    impressions should have length 2
    impressions.head.search shouldBe search.request
    impressions.map(_.videoId) shouldBe List(VideoId("v1"), VideoId("v2"))
    impressions.map(_.interaction) shouldBe List(false, true)
  }

}
