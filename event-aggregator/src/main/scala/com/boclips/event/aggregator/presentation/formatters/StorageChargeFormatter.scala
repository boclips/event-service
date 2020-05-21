package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.{ContentPartnerStorageCharge, VideoStorageCharge}
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object StorageChargeFormatter extends SingleRowFormatter[VideoStorageCharge] {

  override def writeRow(obj: VideoStorageCharge, json: JsonObject): Unit = {
    json.addProperty("id", obj.id)
    json.addProperty("videoId", obj.videoId.value)
    json.addProperty("valueGbp", obj.valueGbp)
    json.addDateProperty("periodStart", obj.periodStart)
    json.addDateProperty("periodEnd", obj.periodEnd)
  }
}
