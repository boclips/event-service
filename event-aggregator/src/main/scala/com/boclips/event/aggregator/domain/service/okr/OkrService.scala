package com.boclips.event.aggregator.domain.service.okr

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.service.Data
import com.boclips.event.aggregator.domain.service.search.SearchAssembler
import com.boclips.event.aggregator.domain.service.session.SessionAssembler
import com.boclips.event.aggregator.utils.Collections
import org.apache.spark.rdd.RDD

object OkrService {

  def computeKeyResults(timePeriodDuration: TimePeriodDuration)(implicit data: Data): List[KeyResults] = {
    data.events.sparkContext.setJobGroup("Calculating OKRs", "")
    implicit val events: RDD[_ <: Event] = data.events
    implicit val users: RDD[User] = data.users
    implicit val sessions: RDD[Session] = new SessionAssembler(events, users, data.dataDescription).assembleSessions()
    implicit val searches: RDD[Search] = new SearchAssembler(sessions).assembleSearches()
    join(
      UserMetricCalculator calculateMetrics timePeriodDuration,
      PlaybackMetricCalculator calculateMetrics timePeriodDuration,
      SearchMetricCalculator calculateMetrics timePeriodDuration
    )
  }

  private def join(userMetrics: List[UserMetric], playbackMetrics: List[PlaybackMetric], searchMetrics: List[SearchMetric]) = {
    Collections.fullJoin(indexByTimePeriod(userMetrics), indexByTimePeriod(playbackMetrics), indexByTimePeriod(searchMetrics))
      .flatMap {
        case (timePeriod, (Some(userMetric), Some(playbackMetric), Some(searchMetric))) => Some(KeyResults(timePeriod, userMetric, playbackMetric, searchMetric))
        case x => println(x); None
      }
      .toList
  }

  private def indexByTimePeriod[TMetric <: Metric](metrics: List[TMetric]): Map[DateRange, TMetric] = {
    metrics.map { metric => (metric.timePeriod, metric) }.toMap
  }
}
