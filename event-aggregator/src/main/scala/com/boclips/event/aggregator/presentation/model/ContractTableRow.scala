package com.boclips.event.aggregator.presentation.model

import com.boclips.event.aggregator.domain.model.ContractLegalRestriction
import com.boclips.event.aggregator.domain.model.contentpartners.Contract

case class ContractTableRow(
                           contract: Contract,
                           clientFacingRestrictions: List[ContractLegalRestriction],
                           )
