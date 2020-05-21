package com.boclips.event.aggregator.domain.service.contentpartners

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createVideo

class ContentPartnerAssemblerTest extends IntegrationTest {

  it should "create one instance of each content partner" in sparkTest { implicit spark =>
    val videos = rdd(
      createVideo(id = "1", contentPartner = "TED"),
      createVideo(id = "2", contentPartner = "TED"),
      createVideo(id = "3", contentPartner = "AP")
    )

    val contentPartners = ContentPartnerAssembler(videos).collect.toList
    contentPartners should have size 2
  }

}
