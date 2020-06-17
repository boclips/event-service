package com.boclips.event.aggregator.presentation.formatters

import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

import com.boclips.event.aggregator.domain.model.Query
import com.boclips.event.aggregator.domain.model.events.CollectionInteractedWithEvent
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object CollectionInteractionEventsFormatter extends SingleRowFormatter[CollectionInteractedWithEvent] {
  override def writeRow(event: CollectionInteractedWithEvent, json: JsonObject): Unit = {
    json.addProperty("timestamp", event.timestamp.format(ISO_OFFSET_DATE_TIME))
    json.addProperty("userId", event.userIdentity.id.map(_.value))
    json.addProperty("urlPath", event.url.map(_.path))
    json.addProperty("urlHost", event.url.map(_.host))
    json.addProperty("urlParams", event.url.map(_.rawParams))
    json.addProperty("collectionId", event.collectionId.value)
    json.addProperty("subtype", event.subtype)
    json.addProperty("query", event.query.getOrElse(Query("")).normalized())
  }
}
