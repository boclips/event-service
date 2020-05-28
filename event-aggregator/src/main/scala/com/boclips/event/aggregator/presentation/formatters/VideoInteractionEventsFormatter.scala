package com.boclips.event.aggregator.presentation.formatters

import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.util.UUID

import com.boclips.event.aggregator.domain.model.Query
import com.boclips.event.aggregator.domain.model.events.VideoInteractedWithEvent
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object VideoInteractionEventsFormatter extends SingleRowFormatter[VideoInteractedWithEvent] {
  override def writeRow(event: VideoInteractedWithEvent, json: JsonObject): Unit = {
    json.addProperty("timestamp", event.timestamp.format(ISO_OFFSET_DATE_TIME))
    json.addProperty("userId", event.userId.value)
    json.addProperty("urlPath", event.url.map(_.path))
    json.addProperty("urlHost", event.url.map(_.host))
    json.addProperty("urlParams", event.url.map(_.rawParams))
    json.addProperty("videoId", event.videoId.value)
    json.addProperty("subtype", event.subtype.getOrElse(""))
    json.addProperty("query", event.query.getOrElse(Query("")).normalized())
    json.addProperty("id", UUID.randomUUID().toString)
  }
}
