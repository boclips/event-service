package com.boclips.event.aggregator.domain.service.collection

import com.boclips.event.aggregator.domain.model
import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.search.CollectionImpression
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createSearch, createSearchResponse}


class CollectionSearchResultImpressionAssemblerTest extends IntegrationTest {

  it should "create one impression for each collection" in sparkTest { implicit spark =>
    val search = createSearch(response = createSearchResponse(collectionResults = Set(
      CollectionImpression(collectionId = CollectionId("c1"), interaction = true),
      model.search.CollectionImpression(collectionId = CollectionId("c2"), interaction = false),
      model.search.CollectionImpression(collectionId = CollectionId("c3"), interaction = true),
    )))

    val searches = rdd(search)

    val impressions = CollectionSearchResultImpressionAssembler(searches).collect().toList

    impressions should have size 3
    impressions.map(_.interaction) shouldBe List(true, false, true)

  }
}
