package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createPageRenderedEvent

class PagesRenderedFormatterTest extends Test {

  it should "write the url host" in {
    val json = PagesRenderedFormatter formatRow createPageRenderedEvent(url = "http://ricky-gervais.teachers.com/videos/test?data=thistest&age=59")

    json.get("urlHost").getAsString shouldBe "ricky-gervais.teachers.com"
    json.get("urlPath").getAsString shouldBe "/videos/test"
    json.get("urlParams").getAsString shouldBe "data=thistest&age=59"
  }

}
