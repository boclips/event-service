package com.boclips.event.aggregator.presentation.formatters

import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

import com.boclips.event.aggregator.domain.model.Search
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object SearchFormatter extends SingleRowFormatter[Search] {

  override def writeRow(obj: Search, json: JsonObject): Unit = {
    val Search(request, response, interactions) = obj

    val videoInteractionCount = response.videoResults.count(_.interaction)
    val collectionInteractionCount = response.collectionResults.count(_.interaction)

    json.addProperty("timestamp", request.timestamp.format(ISO_OFFSET_DATE_TIME))
    json.addProperty("userId", request.userIdentity.id.map(_.value))
    json.addProperty("query", request.query.normalized())
    json.addProperty("resultPagesSeen", interactions.resultPagesSeen)
    json.addProperty("videosPlayed", interactions.videosPlayed.size)
    json.addProperty("videoSecondsPlayed", interactions.videosPlayed.map(_.secondsPlayed).sum)
    json.addProperty("totalResults", response.totalResults)
    json.addProperty("minResults", response.minResults)
    json.addProperty("collectionResultsCount", response.collectionResults.size)
    json.addProperty("interactionCount", videoInteractionCount + collectionInteractionCount)
    json.addProperty("videoInteractionCount", videoInteractionCount)
    json.addProperty("collectionInteractionCount", collectionInteractionCount)
    json.addProperty("urlHost", request.url.map(_.host))
    json.addProperty("urlPath", request.url.map(_.path))
    json.addStringArrayProperty("urlParamKeys", request.urlParamsKeys.toList)
    json.addProperty("id", request.id)

  }
}
