package com.boclips.event.aggregator.presentation.formatters

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{CollectionId, CollectionSearchResultImpression, Url}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory._

class CollectionSearchResultImpressionFormatterTest extends Test {
  it should "write timestamp" in {
    val impression:CollectionSearchResultImpression = {
      createCollectionSearchResultImpression(search = createSearchRequest(timestamp = ZonedDateTime.of(2012, 4, 23, 18, 25, 43, 511000000, ZoneOffset.UTC)))
    }

    val json = CollectionSearchResultImpressionFormatter formatRow impression

    json.get("timestamp").getAsString shouldBe "2012-04-23T18:25:43.511Z"
  }

  it should "write impression interacted with" in {
    val impression: CollectionSearchResultImpression = createCollectionSearchResultImpression(interaction = false)
    val json = CollectionSearchResultImpressionFormatter formatRow impression
    json.get("interaction").getAsBoolean shouldBe false

  }

  it should "write user id" in {
    val impression = createCollectionSearchResultImpression(search = createSearchRequest(userId = "dave"))

    val json = CollectionSearchResultImpressionFormatter formatRow impression

    json.get("userId").getAsString shouldBe "dave"
  }

  it should "write collection id" in {
    val impression = createCollectionSearchResultImpression(collectionId = CollectionId("c-1"))
    val json = CollectionSearchResultImpressionFormatter formatRow impression
    json.get("collectionId").getAsString shouldBe "c-1"
  }

  it should "write url host path and params" in {
    val impression = createCollectionSearchResultImpression(search = createSearchRequest(url = Some(Url.parse("https://boclips.com/bla?page=1&q=aa"))))

    val json = CollectionSearchResultImpressionFormatter formatRow impression

    json.getString("urlHost") shouldBe "boclips.com"
    json.getString("urlPath") shouldBe "/bla"
    json.getString("urlRawParams") shouldBe "page=1&q=aa"
  }


}
