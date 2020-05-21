package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.KeyResultsFormatter

trait Metric {
  val timePeriod: DateRange
}

case class KeyResults(timePeriod: DateRange, userStats: UserMetric, playbackMetric: PlaybackMetric, searchStats: SearchMetric)

object KeyResults {
  implicit val formatter: RowFormatter[KeyResults] = KeyResultsFormatter
}

case class PlaybackMetric(timePeriod: DateRange, totalSecondsWatched: Long, medianSecondsWatched: Long) extends Metric

case class SearchMetric(timePeriod: DateRange, percentageSearchesLeadingToPlayback: Double, percentagePlaybacksTop3: Double) extends Metric

case class UserMetric(
                       timePeriod: DateRange,
                       activeUserCounts: ActiveUserCounts,
                       totalsAtStart: UserTotals,
                       totalsAtEnd: UserTotals,
                       recentlyActiveAtStart: Long,
                       recentlyActiveAtEnd: Long
                     ) extends Metric {
  def repeatRate: Double = activeUserCounts.repeatUsers.toDouble / activeUserCounts.totalUsers

  def churnRate: Double = (recentlyActiveAtStart + activeUserCounts.newUsers - recentlyActiveAtEnd).toDouble / recentlyActiveAtStart

  def newAccounts: Long = totalsAtEnd.totalAccounts - totalsAtStart.totalAccounts
}

case class ActiveUserCounts(newUsers: Int, repeatUsers: Int) {
  def totalUsers: Int = newUsers + repeatUsers

  def +(that: ActiveUserCounts): ActiveUserCounts = ActiveUserCounts(newUsers + that.newUsers, repeatUsers + that.repeatUsers)
}
