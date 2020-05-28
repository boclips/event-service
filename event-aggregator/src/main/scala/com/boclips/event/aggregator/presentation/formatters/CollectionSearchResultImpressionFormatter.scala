package com.boclips.event.aggregator.presentation.formatters

import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.util.UUID

import com.boclips.event.aggregator.domain.model.CollectionSearchResultImpression
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object CollectionSearchResultImpressionFormatter extends SingleRowFormatter[CollectionSearchResultImpression] {
  override def writeRow(impression: CollectionSearchResultImpression, json: JsonObject): Unit = {
    json.addProperty("timestamp", impression.search.timestamp.format(ISO_OFFSET_DATE_TIME))
    json.addProperty("collectionId", impression.collectionId.value)
    json.addProperty("interaction", impression.interaction)
    json.addProperty("query", impression.search.query.normalized())
    json.addProperty("urlHost", impression.search.url.map(_.host))
    json.addProperty("urlPath", impression.search.url.map(_.path))
    json.addProperty("urlRawParams", impression.search.url.map(_.rawParams))
    json.addProperty("id", UUID.randomUUID().toString)
    json.addProperty("userId", impression.search.userId.value)
  }
}


