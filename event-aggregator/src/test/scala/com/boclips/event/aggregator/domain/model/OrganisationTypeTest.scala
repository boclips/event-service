package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.domain.model.users.{API_ORGANISATION, DISTRICT_ORGANISATION, OrganisationType, SCHOOL_ORGANISATION}
import com.boclips.event.aggregator.testsupport.Test

class OrganisationTypeTest extends Test {

  "from string" should "return case matching value" in {
    OrganisationType.from("API") shouldBe API_ORGANISATION
    OrganisationType.from("SCHOOL") shouldBe SCHOOL_ORGANISATION
    OrganisationType.from("DISTRICT") shouldBe DISTRICT_ORGANISATION
  }

  it should "throw when no case matches" in {
    assertThrows[IllegalArgumentException] {
      OrganisationType.from("school")
    }
  }

}
