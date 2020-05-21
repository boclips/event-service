package com.boclips.event.aggregator.presentation.formatters

import java.time.ZonedDateTime

import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object DataVersionFormatter extends SingleRowFormatter[ZonedDateTime] {

  override def writeRow(date: ZonedDateTime, json: JsonObject): Unit = {
    json.addDateTimeProperty("version", date)
  }
}
