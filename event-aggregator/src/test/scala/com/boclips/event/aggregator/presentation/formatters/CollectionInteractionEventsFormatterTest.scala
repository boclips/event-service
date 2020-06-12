package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createCollectionInteractedWithEvent
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity

class CollectionInteractionEventsFormatterTest extends Test {

  it should "format the collection interaction event" in {
    val json = CollectionInteractionEventsFormatter formatRow createCollectionInteractedWithEvent(
      url = "http://test.com/videos/test?q=apple",
      userIdentity = createBoclipsUserIdentity("user-id"),
      collectionId = "collection-id",
      subtype = Some("NAVIGATE_TO_COLLECTION_DETAILS"),
      query = Some("apple"))

    json.get("urlHost").getAsString shouldBe "test.com"
    json.get("urlPath").getAsString shouldBe "/videos/test"
    json.get("urlParams").getAsString shouldBe "q=apple"
    json.get("userId").getAsString shouldBe "user-id"
    json.get("collectionId").getAsString shouldBe "collection-id"
    json.get("subtype").getAsString shouldBe "NAVIGATE_TO_COLLECTION_DETAILS"
    json.get("query").getAsString shouldBe "apple"
  }
}
