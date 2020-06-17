package com.boclips.event.aggregator.domain.service.search

import com.boclips.event.aggregator.domain.model.okrs.TimePeriodDuration
import com.boclips.event.aggregator.domain.model.search.{QueryScore, Search}
import com.boclips.event.aggregator.domain.model.search
import org.apache.spark.rdd.RDD

class QueryScorer(priorHits: Int, priorMisses: Int) {

  def scoreQueries(searchEvents: RDD[Search], timePeriodDuration: TimePeriodDuration): RDD[QueryScore] = {

    val prior = BetaDistribution(priorHits, priorMisses)

    searchEvents
      .map(event => ((timePeriodDuration.dateRangeOf(event.request.timestamp), event.request.query.normalized(), event.request.userIdentity), event.interactions.videosPlayed.nonEmpty))
      .groupByKey()
      .mapValues(anyPlaybacks => anyPlaybacks.toList.contains(true))
      .map { case ((timePeriod, query, _), hit) => ((timePeriod, query), hit) }
      .aggregateByKey(BetaDistribution(0, 0))(_.add(_), _.add(_))
      .map { case ((timePeriod, query), distribution) => search.QueryScore(
        timePeriod = timePeriod,
        query = query,
        count = distribution.total(),
        hits = distribution.alpha,
        score = prior.add(distribution).mean())
      }
  }

}
