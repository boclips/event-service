package com.boclips.event.service.domain

import com.boclips.eventbus.domain.ContractLegalRestriction

interface ContractLegalRestrictionsRepository {
    fun save(contractLegalRestriction: ContractLegalRestriction)
}