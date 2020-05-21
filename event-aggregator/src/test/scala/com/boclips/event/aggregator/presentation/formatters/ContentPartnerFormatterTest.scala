package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createContentPartner

class ContentPartnerFormatterTest extends Test {

  it should "format json" in {
    val json = ContentPartnerFormatter formatRow createContentPartner(
      name = "content partner name",
      playbackProvider = "YOUTUBE",
    )

    json.get("name").getAsString shouldBe "content partner name"
    json.get("playbackProvider").getAsString shouldBe "YOUTUBE"
  }
}
