package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.contentpartners.Contract
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.{JsonArray, JsonObject}

object ContractFormatter extends SingleRowFormatter[Contract] {
  override def writeRow(obj: Contract, json: JsonObject): Unit = {
    json.addProperty("id", obj.id.value)
    json.addProperty("name", obj.name)
    json.addProperty("contractDocumentLink", obj.contractDocumentLink.orNull)
    json.addProperty("contractIsRolling", obj.contractIsRolling.map(Boolean.box).orNull)
    json.addDateProperty("contractStartDate", obj.contractDates.flatMap(_.start).orNull)
    json.addDateProperty("contractEndDate", obj.contractDates.flatMap(_.end).orNull)
    json.addProperty("daysBeforeTerminationWarning", obj.daysBeforeTerminationWarning.map(Int.box).orNull)
    json.addProperty("yearsForMaximumLicense", obj.yearsForMaximumLicense.map(Int.box).orNull)
    json.addProperty("daysForSellOffPeriod", obj.daysForSellOffPeriod.map(Int.box).orNull)
    json.addProperty("downloadRoyaltySplit", obj.royaltySplit.flatMap(_.download).map(Float.box).orNull)
    json.addProperty("streamingRoyaltySplit", obj.royaltySplit.flatMap(_.streaming).map(Float.box).orNull)
    json.addProperty("minimumPriceDescription", obj.minimumPriceDescription.orNull)
    json.addProperty("remittanceCurrency", obj.remittanceCurrency.map(_.toString).orNull)

    obj.restrictions match {
      case Some(restrictions) =>
        json.addStringArrayProperty("clientFacingRestrictions", restrictions.clientFacing.orNull)
        json.addProperty("territoryRestrictions", restrictions.territory.orNull)
        json.addProperty("licensingRestrictions", restrictions.licensing.orNull)
        json.addProperty("editingRestrictions", restrictions.editing.orNull)
        json.addProperty("marketingRestrictions", restrictions.marketing.orNull)
        json.addProperty("companiesRestrictions", restrictions.companies.orNull)
        json.addProperty("payoutRestrictions", restrictions.payout.orNull)
        json.addProperty("otherRestrictions", restrictions.other.orNull)
      case None =>
    }

    val costs = obj.costs

    val minimumGuarantees = new JsonArray()
    obj.costs.minimumGuarantee.zipWithIndex.foreach { case (mg: BigDecimal, index: Int) =>
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
