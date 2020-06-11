package com.boclips.event.aggregator.presentation.formatters

import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

import com.boclips.event.aggregator.domain.model.events.PageRenderedEvent
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object PagesRenderedFormatter extends SingleRowFormatter[PageRenderedEvent] {
  override def writeRow(pageRendered: PageRenderedEvent, json: JsonObject): Unit = {
    json.addProperty("timestamp", pageRendered.timestamp.format(ISO_OFFSET_DATE_TIME))
    json.addProperty("userId", pageRendered.userId.map(_.value))
    json.addProperty("urlPath", pageRendered.url.map(_.path))
    json.addProperty("urlHost", pageRendered.url.map(_.host))
    json.addProperty("urlParams", pageRendered.url.map(_.rawParams))
  }

}
