package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.ContentPartnerStorageCharge
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object ContentPartnerStorageChargeFormatter extends SingleRowFormatter[ContentPartnerStorageCharge] {

  override def writeRow(obj: ContentPartnerStorageCharge, json: JsonObject): Unit = {
    json.addProperty("id", obj.id)
    json.addProperty("contentPartner", obj.contentPartner)
    json.addProperty("valueGbp", obj.valueGbp)
    json.addDateProperty("periodStart", obj.periodStart)
    json.addDateProperty("periodEnd", obj.periodEnd)
  }
}
