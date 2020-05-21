package com.boclips.event.aggregator.presentation.formatters

import java.time.ZoneOffset

import com.boclips.event.aggregator.domain.model.QueryScore
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object QueryScoreFormatter extends SingleRowFormatter[QueryScore] {

  override def writeRow(queryScore: QueryScore, json: JsonObject): Unit = {
    json.addDateTimeProperty("start", queryScore.timePeriod.startInclusive)
    json.addDateTimeProperty("end", queryScore.timePeriod.endExclusive)
    json.addMonthProperty("month", queryScore.timePeriod.startInclusive)
    json.addProperty("query", queryScore.query)
    json.addProperty("score", queryScore.score)
    json.addProperty("count", queryScore.count)
    json.addProperty("hits", queryScore.hits)
  }
}
