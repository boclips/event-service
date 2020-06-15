package com.boclips.event.service.application

import com.boclips.event.infrastructure.contract.ContractDocument
import com.boclips.event.service.infrastructure.mongodb.MongoContractRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.ContractFactory
import com.boclips.eventbus.events.contract.BroadcastContractRequested
import com.boclips.eventbus.events.contract.ContractUpdated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateContractIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `insert a contract when update event is received`() {
        val contract = ContractFactory.createContract(
            id = "contract-id",
            name = "contract channel title"
        )

        eventBus.publish(ContractUpdated(contract))

        val document = getSingleDocument()
        assertThat(document.id).isEqualTo(contract.contractId.value)
        assertThat(document.name).isEqualTo(contract.name)
    }

    @Test
    fun `insert a contract when broadcast event is received`() {
        val contract = ContractFactory.createContract(
            id = "contract-id",
            name = "contract channel title"
        )

        eventBus.publish(BroadcastContractRequested(contract))

        val document = getSingleDocument()
        assertThat(document.id).isEqualTo(contract.contractId.value)
        assertThat(document.name).isEqualTo(contract.name)
    }

    fun getSingleDocument() = document<ContractDocument>(MongoContractRepository.COLLECTION_NAME)
}