package com.boclips.event.aggregator.domain.service.okr

import com.boclips.event.aggregator.domain.model.events.{Event, VideoSegmentPlayedEvent}
import com.boclips.event.aggregator.domain.model.{DateRange, PlaybackMetric, TimePeriodDuration}
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.spark.rdd.RDD

object PlaybackMetricCalculator {

  def calculateMetrics(timePeriodDuration: TimePeriodDuration)(implicit events: RDD[_ <: Event]): List[PlaybackMetric] = {
    events.sparkContext.setJobDescription("Calculating playback metrics")
    totalPlaybackSecondsByUser(timePeriodDuration)
      .map {
        case (timePeriod, totalSecondsWatchedByUser) =>
          val totalSeconds = totalSecondsWatchedByUser.sum
          val medianSeconds = PlaybackMetricCalculator.median(totalSecondsWatchedByUser).toLong
          PlaybackMetric(timePeriod = timePeriod, totalSecondsWatched = totalSeconds, medianSecondsWatched = medianSeconds)
      }
      .collect()
      .toList
  }

  private def totalPlaybackSecondsByUser(timePeriodDuration: TimePeriodDuration)(implicit events: RDD[_ <: Event]): RDD[(DateRange, Iterable[Long])] = {
    events
      .flatMap {
        case e: VideoSegmentPlayedEvent => Some(((timePeriodDuration.dateRangeOf(e.timestamp), e.userId), e.secondsWatched.toLong))
        case otherEvent => Some(((timePeriodDuration.dateRangeOf(otherEvent.timestamp), otherEvent.userId), 0L))
      }
      .groupByKey()
      .mapValues(_.sum)
      .map {
        case ((timePeriod, _), secondsWatched) => (timePeriod, secondsWatched)
      }
      .groupByKey()
  }

  private def median(xs: Iterable[Long]): Double = {
    val stats = new DescriptiveStatistics()
    xs.foreach(stats.addValue(_))
    stats.getPercentile(50)
  }
}
