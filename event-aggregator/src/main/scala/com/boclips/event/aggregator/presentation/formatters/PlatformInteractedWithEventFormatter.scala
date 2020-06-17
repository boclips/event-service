package com.boclips.event.aggregator.presentation.formatters

import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

import com.boclips.event.aggregator.domain.model.events.PlatformInteractedWithEvent
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object PlatformInteractedWithEventFormatter extends SingleRowFormatter[PlatformInteractedWithEvent] {
  override def writeRow(interaction: PlatformInteractedWithEvent, json: JsonObject): Unit = {
    json.addProperty("timestamp", interaction.timestamp.format(ISO_OFFSET_DATE_TIME))
    json.addProperty("userId", interaction.userIdentity.id.map(_.value))
    json.addProperty("subtype",interaction.subtype)
    json.addProperty("urlPath", interaction.url.map(_.path))
    json.addProperty("urlHost", interaction.url.map(_.host))
    json.addProperty("urlParams", interaction.url.map(_.rawParams))
  }
}
