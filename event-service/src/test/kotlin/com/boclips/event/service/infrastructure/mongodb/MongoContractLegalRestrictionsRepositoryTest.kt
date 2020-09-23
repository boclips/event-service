package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.ContractLegalRestrictionDocument
import com.boclips.event.infrastructure.contract.ContractDocument
import com.boclips.event.service.domain.ContractLegalRestrictionsRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.eventbus.domain.ContractLegalRestriction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MongoContractLegalRestrictionsRepositoryTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var repository: ContractLegalRestrictionsRepository

    @Test
    fun `save and update contract legal restrictions by id`() {
        val id = "id-123"
        repository.save(
                ContractLegalRestriction.builder()
                        .id(id)
                        .text("No Trespassing")
                        .build()
        )

        val created = getSingleDocument()

        assertThat(created.id).isEqualTo("id-123")
        assertThat(created.text).isEqualTo("No Trespassing")

        repository.save(
                ContractLegalRestriction.builder()
                        .id(id)
                        .text("Trespassing allowed")
                        .build()
        )

        val updated = getSingleDocument()

        assertThat(updated.id).isEqualTo("id-123")
        assertThat(updated.text).isEqualTo("Trespassing allowed")
    }
    fun getSingleDocument() = document<ContractLegalRestrictionDocument>(MongoContractLegalRestrictionsRepository.COLLECTION_NAME)
}