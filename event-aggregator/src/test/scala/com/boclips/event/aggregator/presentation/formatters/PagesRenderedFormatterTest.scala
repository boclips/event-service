package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createPageRenderedEvent
import com.google.gson.JsonNull

class PagesRenderedFormatterTest extends Test {

  it should "write the url host" in {
    val json = PagesRenderedFormatter formatRow createPageRenderedEvent(
      url = "http://ricky-gervais.teachers.com/videos/test?data=thistest&age=59"
    )

    json.get("urlHost").getAsString shouldBe "ricky-gervais.teachers.com"
    json.get("urlPath").getAsString shouldBe "/videos/test"
    json.get("urlParams").getAsString shouldBe "data=thistest&age=59"
  }

  it should "format the viewport from page rendered event" in {
    val json = PagesRenderedFormatter formatRow createPageRenderedEvent(
      url = "http://ricky-gervais.teachers.com/videos/test?data=thistest&age=59",
      viewportHeight = Some(100),
      viewportWidth = Some(99)
    )

    json.get("viewportWidth").getAsInt shouldBe 99
    json.get("viewportHeight").getAsInt shouldBe 100
  }

  it should "ignore the viewport if not present" in {
    val json = PagesRenderedFormatter formatRow createPageRenderedEvent(
      url = "http://ricky-gervais.teachers.com/videos/test?data=thistest&age=59",
      viewportHeight = None,
      viewportWidth = None
    )

    json.get("viewportWidth") shouldBe JsonNull.INSTANCE
    json.get("viewportHeight") shouldBe JsonNull.INSTANCE
  }

}
