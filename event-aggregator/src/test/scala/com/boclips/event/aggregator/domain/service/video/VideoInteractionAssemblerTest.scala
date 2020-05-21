package com.boclips.event.aggregator.domain.service.video

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory

class VideoInteractionAssemblerTest extends IntegrationTest {

  it should "assemble video interaction events" in sparkTest { implicit spark =>
    val events = rdd(
      EventFactory.createVideoInteractedWithEvent(),
      EventFactory.createCollectionInteractedWithEvent(),
      EventFactory.createPageRenderedEvent(),
      EventFactory.createVideosSearchedEvent(),
    )

    val interactionEvents = VideoInteractionAssembler(events).collect()

    interactionEvents should have length 1
  }
}
