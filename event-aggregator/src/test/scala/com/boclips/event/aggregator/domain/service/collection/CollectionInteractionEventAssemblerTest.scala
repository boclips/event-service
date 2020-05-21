package com.boclips.event.aggregator.domain.service.collection

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.{createCollectionInteractedWithEvent, createPageRenderedEvent, createVideoSegmentPlayedEvent, createVideosSearchedEvent}

class CollectionInteractionEventAssemblerIntegrationTest extends IntegrationTest {

  it should "assemble the collection interaction events" in sparkTest { implicit spark =>
    val events = rdd(
      createVideoSegmentPlayedEvent(),
      createVideoSegmentPlayedEvent(),
      createPageRenderedEvent(),
      createCollectionInteractedWithEvent(),
      createVideosSearchedEvent(),
      createVideosSearchedEvent(),
      createCollectionInteractedWithEvent(),
    )

    val interactionEvents = CollectionInteractionEventAssembler(events).collect()

    interactionEvents should have length 2
  }
}
