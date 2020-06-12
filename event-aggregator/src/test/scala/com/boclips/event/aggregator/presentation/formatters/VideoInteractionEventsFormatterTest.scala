package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoInteractedWithEvent
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity

class VideoInteractionEventsFormatterTest extends Test {
  it should "format video interaction event" in {
    val json = VideoInteractionEventsFormatter formatRow createVideoInteractedWithEvent(
      userIdentity = createBoclipsUserIdentity("user-id"),
      videoId = "video-id",
      subtype = Some("HAVE_FUN_WITH_VIDEO"),
      query = Some("apple"),
      url = "http://test.com/videos/test?q=apple",
    )
    json.get("urlHost").getAsString shouldBe "test.com"
    json.get("urlPath").getAsString shouldBe "/videos/test"
    json.get("urlParams").getAsString shouldBe "q=apple"
    json.get("userId").getAsString shouldBe "user-id"
    json.get("videoId").getAsString shouldBe "video-id"
    json.get("subtype").getAsString shouldBe "HAVE_FUN_WITH_VIDEO"
    json.get("query").getAsString shouldBe "apple"
  }
}


