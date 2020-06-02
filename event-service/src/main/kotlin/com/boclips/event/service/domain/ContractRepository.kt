package com.boclips.event.service.domain

import com.boclips.eventbus.domain.contract.Contract

interface ContractRepository {
    fun save(contract: Contract)
}