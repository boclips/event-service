package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.domain.model.users.{API_ORGANISATION, DISTRICT_ORGANISATION, LTI_DEPLOYMENT, OrganisationType, SCHOOL_ORGANISATION}
import com.boclips.event.aggregator.testsupport.Test

class OrganisationTypeTest extends Test {

  "from string" should "return case matching value" in {
    OrganisationType.from("API") shouldBe API_ORGANISATION
    OrganisationType.from("SCHOOL") shouldBe SCHOOL_ORGANISATION
    OrganisationType.from("DISTRICT") shouldBe DISTRICT_ORGANISATION
    OrganisationType.from("LTI_DEPLOYMENT") shouldBe LTI_DEPLOYMENT
  }

  it should "throw when no case matches" in {
    assertThrows[IllegalArgumentException] {
      OrganisationType.from("school")
    }
  }

}
