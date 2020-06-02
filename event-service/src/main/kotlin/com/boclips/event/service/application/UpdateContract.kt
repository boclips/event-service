package com.boclips.event.service.application

import com.boclips.event.service.domain.ContractRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.domain.contract.Contract
import com.boclips.eventbus.events.contract.BroadcastContractRequested
import com.boclips.eventbus.events.contract.ContractUpdated

class UpdateContract(private val repository: ContractRepository) {
    @BoclipsEventListener
    fun channelUpdated(event: ContractUpdated) {
        save(event.contract)
    }

    @BoclipsEventListener
    fun broadcastChannelRequested(event: BroadcastContractRequested) {
        save(event.contract)
    }

    private fun save(contract: Contract) {
        repository.save(contract)
    }
}
