package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory

class PlatformInteractedWithEventFormatterTest extends Test {
  it should "write fields" in {
    val json = PlatformInteractedWithEventFormatter formatRow EventFactory.createPlatformInteractedWithEvent(
      subtype = Some("BACK_TO_BLACK"),
      url = "http://know.teachers.com/videos/test?data=thistest&age=59"
    )

    json.get("subtype").getAsString shouldBe "BACK_TO_BLACK"
    json.get("urlHost").getAsString shouldBe "know.teachers.com"
    json.get("urlPath").getAsString shouldBe "/videos/test"
    json.get("urlParams").getAsString shouldBe "data=thistest&age=59"

  }
}
