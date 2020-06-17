package com.boclips.event.aggregator.domain.service.okr

import com.boclips.event.aggregator.domain.model.okrs.{DateRange, SearchMetric, TimePeriodDuration}
import com.boclips.event.aggregator.domain.model.search.Search
import com.boclips.event.aggregator.domain.model.okrs
import org.apache.spark.rdd.RDD

object SearchMetricCalculator {

  def calculateMetrics(timePeriodDuration: TimePeriodDuration)(implicit searches: RDD[Search]): List[SearchMetric] = {
    searches.sparkContext.setJobDescription("Calculating search metrics")

    val searchesLeadingToPlayback: RDD[(DateRange, Double)] = searches.map { event =>
      (timePeriodDuration dateRangeOf event.request.timestamp, if (event.interactions.videosPlayed.nonEmpty) 1.0 else 0.0)
    }
      .groupByKey()
      .mapValues(SearchMetricCalculator.mean)

    def top3(event: Search) = event.interactions.videosPlayed.map(_.videoIndex.getOrElse(Int.MaxValue)).min < 3

    val playbackTop3: RDD[(DateRange, Double)] = searches
      .filter(event => event.interactions.videosPlayed.nonEmpty)
      .map { event =>
        (timePeriodDuration dateRangeOf event.request.timestamp, if (top3(event)) 1.0 else 0.0)
      }
      .groupByKey()
      .mapValues(SearchMetricCalculator.mean)

    searchesLeadingToPlayback.fullOuterJoin(playbackTop3).map {
      case (timePeriod, (maybeSearchesLeadingToPlayback, maybePlaybackTop3)) => okrs.SearchMetric(timePeriod, maybeSearchesLeadingToPlayback.getOrElse(0), maybePlaybackTop3.getOrElse(0))
    }
      .collect().toList
  }

  def mean(xs: Iterable[Double]): Double = if (xs.nonEmpty) xs.sum / xs.size else 0.0
}
