package com.boclips.event.service.application

import com.boclips.event.infrastructure.ContractLegalRestrictionDocument
import com.boclips.event.service.infrastructure.mongodb.MongoContractLegalRestrictionsRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.eventbus.domain.ContractLegalRestriction
import com.boclips.eventbus.events.contractlegalrestriction.ContractLegalRestrictionBroadcastRequested
import com.boclips.eventbus.events.contractlegalrestriction.ContractLegalRestrictionUpdated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateContractLegalRestrictionIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `insert a contract legal restriction when Update & Broadcast`() {
        val contractLegalRestriction = ContractLegalRestriction.builder()
                .id("id-0")
                .text("my-own-rules")
                .build()

        eventBus.publish(ContractLegalRestrictionUpdated(contractLegalRestriction))

        val document = getSingleDocument()

        assertThat(document.id).isEqualTo(contractLegalRestriction.id)
        assertThat(document.text).isEqualTo(contractLegalRestriction.text)

        val contractLegalRestriction2 = ContractLegalRestriction.builder()
                .id("id-0")
                .text("my-own-rules-change")
                .build()
        eventBus.publish(ContractLegalRestrictionBroadcastRequested(contractLegalRestriction2))

        val document2 = getSingleDocument()

        assertThat(document2.id).isEqualTo(contractLegalRestriction2.id)
        assertThat(document2.text).isEqualTo(contractLegalRestriction2.text)

    }

    fun getSingleDocument() = document<ContractLegalRestrictionDocument>(MongoContractLegalRestrictionsRepository.COLLECTION_NAME)
}