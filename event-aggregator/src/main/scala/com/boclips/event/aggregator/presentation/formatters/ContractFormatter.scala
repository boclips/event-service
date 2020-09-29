package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.boclips.event.aggregator.presentation.model.ContractTableRow
import com.google.gson.{JsonArray, JsonObject}

object ContractFormatter extends SingleRowFormatter[ContractTableRow] {
  override def writeRow(obj: ContractTableRow, json: JsonObject): Unit = {
    val contract = obj.contract
    json.addProperty("id", contract.id.value)
    json.addProperty("name", contract.name)
    json.addProperty("contractDocumentLink", contract.contractDocumentLink.orNull)
    json.addProperty("contractIsRolling", contract.contractIsRolling.map(Boolean.box).orNull)
    json.addDateProperty("contractStartDate", contract.contractDates.flatMap(_.start).orNull)
    json.addDateProperty("contractEndDate", contract.contractDates.flatMap(_.end).orNull)
    json.addProperty("daysBeforeTerminationWarning", contract.daysBeforeTerminationWarning.map(Int.box).orNull)
    json.addProperty("yearsForMaximumLicense", contract.yearsForMaximumLicense.map(Int.box).orNull)
    json.addProperty("daysForSellOffPeriod", contract.daysForSellOffPeriod.map(Int.box).orNull)
    json.addProperty("downloadRoyaltySplit", contract.royaltySplit.flatMap(_.download).map(Float.box).orNull)
    json.addProperty("streamingRoyaltySplit", contract.royaltySplit.flatMap(_.streaming).map(Float.box).orNull)
    json.addProperty("minimumPriceDescription", contract.minimumPriceDescription.orNull)
    json.addProperty("remittanceCurrency", contract.remittanceCurrency.map(_.toString).orNull)

    contract.restrictions match {
      case Some(restrictions) =>
        json.addStringArrayProperty("clientFacingRestrictions", obj.clientFacingRestrictions.map(_.text))
        json.addProperty("territoryRestrictions", restrictions.territory.orNull)
        json.addProperty("licensingRestrictions", restrictions.licensing.orNull)
        json.addProperty("editingRestrictions", restrictions.editing.orNull)
        json.addProperty("marketingRestrictions", restrictions.marketing.orNull)
        json.addProperty("companiesRestrictions", restrictions.companies.orNull)
        json.addProperty("payoutRestrictions", restrictions.payout.orNull)
        json.addProperty("otherRestrictions", restrictions.other.orNull)
      case None =>
    }

    val costs = contract.costs

    val minimumGuarantees = new JsonArray()
    contract.costs.minimumGuarantee.zipWithIndex.foreach { case (mg: BigDecimal, index: Int) =>
      val thisJson = new JsonObject()
      thisJson.addProperty("amount", mg)
      thisJson.addProperty("contractYear", index + 1)
      minimumGuarantees.add(thisJson)
    }
    json.add("minimumGuarantee", minimumGuarantees)

    json.addProperty("upfrontLicenseCost", costs.upfrontLicense.orNull)
    json.addProperty("technicalFee", costs.technicalFee.orNull)
    json.addProperty("recoupable", costs.recoupable.map(Boolean.box).orNull)
  }
}
