package com.boclips.event.aggregator.domain.service.search

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideosSearchedEvent
import com.boclips.event.aggregator.testsupport.testfactories.SessionFactory

class SearchAssemblerIntegrationTest extends IntegrationTest {

  it should "assemble searches" in sparkTest { implicit spark =>
    val aggregator = new SearchAssembler(rdd(SessionFactory.createSession(events = List(createVideosSearchedEvent()))))

    aggregator.assembleSearches().collect should not be empty
  }

}
