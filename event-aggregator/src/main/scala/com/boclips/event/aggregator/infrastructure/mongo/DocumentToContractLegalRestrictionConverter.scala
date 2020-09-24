package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.ContractLegalRestriction
import com.boclips.event.infrastructure.ContractLegalRestrictionDocument

object DocumentToContractLegalRestrictionConverter {
  def convert(document: ContractLegalRestrictionDocument): ContractLegalRestriction = {
    ContractLegalRestriction(
      id = document.getId,
      text = document.getText)
  }
}
