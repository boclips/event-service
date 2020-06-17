package com.boclips.event.aggregator.presentation.formatters

import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.util.UUID

import com.boclips.event.aggregator.domain.model.VideoSearchResultImpression
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object VideoSearchResultImpressionFormatter extends SingleRowFormatter[VideoSearchResultImpression] {
  override def writeRow(impression: VideoSearchResultImpression, json: JsonObject): Unit = {
    json.addProperty("timestamp", impression.search.timestamp.format(ISO_OFFSET_DATE_TIME))
    json.addProperty("videoId", impression.videoId.value)
    json.addProperty("interaction", impression.interaction)
    json.addProperty("query", impression.search.query.normalized())
    json.addProperty("userId", impression.search.userIdentity.id.map(_.value))
    json.addProperty("urlHost", impression.search.url.map(_.host))
    json.addProperty("id", UUID.randomUUID().toString)
  }
}
