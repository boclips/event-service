package com.boclips.event.aggregator.domain.service.navigation

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.{createPageRenderedEvent, createVideoSegmentPlayedEvent, createVideosSearchedEvent}

class PagesRenderedAssemblerIntegrationTest  extends IntegrationTest {

  "aggregate pageRendered events" should "filter out PageRenderedEvents" in sparkTest { implicit spark =>
    val events = rdd(
      createVideoSegmentPlayedEvent(),
      createVideoSegmentPlayedEvent(),
      createPageRenderedEvent(),
      createVideosSearchedEvent(),
      createVideosSearchedEvent(),
      createPageRenderedEvent(),
    )

    val pagesRenderedEvents = new PagesRenderedAssembler(events).assemblePagesRendered().collect

    pagesRenderedEvents should have length 2
  }

}
