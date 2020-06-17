package com.boclips.event.aggregator.domain.model.search

import com.boclips.event.aggregator.domain.model.okrs.DateRange
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.QueryScoreFormatter

case class QueryScore(timePeriod: DateRange, query: String, count: Int, hits: Int, score: Double)

object QueryScore {
  implicit val formatter: RowFormatter[QueryScore] = QueryScoreFormatter
}
