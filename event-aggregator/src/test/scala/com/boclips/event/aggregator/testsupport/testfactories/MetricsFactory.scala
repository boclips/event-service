package com.boclips.event.aggregator.testsupport.testfactories

import java.time.Month.{APRIL, MAY}
import java.time.{LocalDate, ZoneOffset}

import com.boclips.event.aggregator.domain.model.okrs.{ActiveUserCounts, DateRange, KeyResults, PlaybackMetric, SearchMetric, UserMetric}
import com.boclips.event.aggregator.domain.model.{okrs, users, _}
import com.boclips.event.aggregator.domain.model.users.UserTotals
import com.boclips.event.aggregator.testsupport.TestTimestamps.thisYearWhole

object MetricsFactory {

  def createKeyResults(
                        timePeriod: DateRange = thisYearWhole(APRIL),
                        userStats: UserMetric = createUserMetric(),
                        playbackMetric: PlaybackMetric = createPlaybackMetric(),
                        searchStats: SearchMetric = createSearchMetric(0)
                      ): KeyResults = {
    okrs.KeyResults(timePeriod = timePeriod, userStats = userStats, playbackMetric = playbackMetric, searchStats = searchStats)
  }

  def createSearchMetric(percentageSearchesLeadingToPlayback: Double = 0, percentagePlaybacksTop3: Double = 0): SearchMetric = {
    okrs.SearchMetric(thisYearWhole(MAY), percentageSearchesLeadingToPlayback, percentagePlaybacksTop3)
  }

  def createUserMetric(
                        timePeriod: DateRange = thisYearWhole(MAY),
                        totalsAtStart: UserTotals = users.UserTotals(date = thisYearWhole(MAY).startInclusive, 4),
                        totalsAtEnd: UserTotals = users.UserTotals(date = thisYearWhole(MAY).endExclusive, 5),
                        activeUserCounts: ActiveUserCounts = ActiveUserCounts(newUsers = 10, repeatUsers = 20),
                        nonChurnedAtStart: Int = 30,
                        nonChurnedAtEnd: Int = 40
                      ): UserMetric = {
    okrs.UserMetric(
      timePeriod,
      activeUserCounts = activeUserCounts,
      totalsAtStart = totalsAtStart,
      totalsAtEnd = totalsAtEnd,
      recentlyActiveAtStart = nonChurnedAtStart,
      recentlyActiveAtEnd = nonChurnedAtEnd
    )
  }

  def createUserTotals(
                        date: LocalDate = LocalDate.now(ZoneOffset.UTC),
                        totalAccounts: Long = 1000
                      ): UserTotals = {
    users.UserTotals(
      date = date,
      totalAccounts = totalAccounts
    )
  }

  def createPlaybackMetric(): PlaybackMetric = {
    okrs.PlaybackMetric(thisYearWhole(MAY), 0, 0)
  }

}
