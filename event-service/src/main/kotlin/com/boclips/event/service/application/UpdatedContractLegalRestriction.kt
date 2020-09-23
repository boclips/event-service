package com.boclips.event.service.application

import com.boclips.event.service.domain.ContractLegalRestrictionsRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.events.contractlegalrestriction.ContractLegalRestrictionBroadcastRequested
import com.boclips.eventbus.events.contractlegalrestriction.ContractLegalRestrictionUpdated


class UpdatedContractLegalRestriction(private val contractLegalRestrictionsRepository: ContractLegalRestrictionsRepository) {

    @BoclipsEventListener
    fun contractLegalRestrictionCreated(event: ContractLegalRestrictionUpdated) {
        contractLegalRestrictionsRepository.save(event.contractLegalRestriction)
    }

    @BoclipsEventListener
    fun contractLegalRestrictionBroadcasted(event: ContractLegalRestrictionBroadcastRequested) {
        contractLegalRestrictionsRepository.save(event.contractLegalRestriction)
    }
}