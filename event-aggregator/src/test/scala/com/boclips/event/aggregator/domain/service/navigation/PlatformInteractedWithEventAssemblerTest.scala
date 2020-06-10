package com.boclips.event.aggregator.domain.service.navigation

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.{createPageRenderedEvent, createVideoSegmentPlayedEvent, createVideosSearchedEvent, createPlatformInteractedWithEvent}

class PlatformInteractedWithEventAssemblerTest extends IntegrationTest {
  "Platform Interacted With Event Assembler " should "extract relevant events" in sparkTest { implicit spark =>
    val events = rdd(
      createVideoSegmentPlayedEvent(),
      createVideoSegmentPlayedEvent(),
      createPageRenderedEvent(),
      createVideosSearchedEvent(),
      createVideosSearchedEvent(),
      createPageRenderedEvent(),
      createPlatformInteractedWithEvent(),
    )
    val platformEvents = new PlatformInteractedWithEventAssembler(events).assemblePlatformInteractedWithEvents().collect()

    platformEvents should have length 1
  }

}
