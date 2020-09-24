package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.ContractLegalRestrictionDocument

class DocumentToContractLegalRestrictionConverterTest extends Test {

  it should "convert the id" in {
    val document = ContractLegalRestrictionDocument.sample()
      .id("id-42")
      .build()

    val legalRestriction = DocumentToContractLegalRestrictionConverter convert document

    legalRestriction.id shouldBe "id-42"
  }
}
