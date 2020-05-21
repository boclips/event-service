package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.Subject
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object SubjectFormatter extends SingleRowFormatter[Subject] {
  override def writeRow(obj: Subject, json: JsonObject): Unit = {
    json.addProperty("name", obj.name)
  }
}
