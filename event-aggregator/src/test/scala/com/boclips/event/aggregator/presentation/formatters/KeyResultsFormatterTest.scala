package com.boclips.event.aggregator.presentation.formatters

import java.time.Month
import java.time.Month.AUGUST

import com.boclips.event.aggregator.domain.model.okrs.{ActiveUserCounts, PlaybackMetric, UserMetric}
import com.boclips.event.aggregator.domain.model.users.UserTotals
import com.boclips.event.aggregator.domain.model.{okrs, users}
import com.boclips.event.aggregator.testsupport.TestTimestamps.wholeMonthOf
import com.boclips.event.aggregator.testsupport.testfactories.MetricsFactory.{createKeyResults, createSearchMetric, createUserTotals}
import com.boclips.event.aggregator.testsupport.{Test, TestTimestamps}

class KeyResultsFormatterTest extends Test {
  it should "write date range bounds" in {
    val json = KeyResultsFormatter formatRow createKeyResults(timePeriod = wholeMonthOf(AUGUST, 2019))

    json.get("start").getAsString shouldBe "2019-08-01T00:00:00Z"
    json.get("end").getAsString shouldBe "2019-09-01T00:00:00Z"
    json.get("month").getAsString shouldBe "2019-08"
  }

  it should "write user stats" in {
    val json = KeyResultsFormatter formatRow createKeyResults(userStats = UserMetric(
      timePeriod = wholeMonthOf(AUGUST, 2019),
      ActiveUserCounts(newUsers = 60, repeatUsers = 40),
      totalsAtStart = UserTotals(date = null, totalAccounts = 1234),
      totalsAtEnd = users.UserTotals(date = null, totalAccounts = 2345),
      recentlyActiveAtStart = 100,
      recentlyActiveAtEnd = 110
    ))

    json.get("activeUsersTotal").getAsInt shouldBe 100
    json.get("activeUsersNew").getAsInt shouldBe 60
    json.get("activeUsersRepeat").getAsInt shouldBe 40
    json.get("newAccounts").getAsInt shouldBe 1111
    json.get("totalAccountsAtEnd").getAsInt shouldBe 2345
    json.get("repeatRate").getAsDouble shouldBe 0.4
    json.get("churnRate").getAsDouble shouldBe 0.5
  }

  it should "write churn as 0 when NaN" in {
    val json = KeyResultsFormatter formatRow createKeyResults(userStats = okrs.UserMetric(
      timePeriod = wholeMonthOf(AUGUST, 2019),
      activeUserCounts = ActiveUserCounts(newUsers = 0, repeatUsers = 0),
      totalsAtStart = createUserTotals(),
      totalsAtEnd = createUserTotals(),
      recentlyActiveAtStart = 0,
      recentlyActiveAtEnd = 0
    ))

    json.get("churnRate").getAsDouble shouldBe 0
  }

  it should "write playback stats" in {
    val json = KeyResultsFormatter formatRow createKeyResults(
      playbackMetric = PlaybackMetric(TestTimestamps.thisYearWhole(Month.JUNE), medianSecondsWatched = 150, totalSecondsWatched = 7300)
    )

    json.get("medianMinsWatched").getAsDouble shouldBe 2.5
    json.get("totalHoursWatched").getAsInt shouldBe 2
  }

  it should "write search stats" in {
    val json = KeyResultsFormatter formatRow createKeyResults(
      searchStats = createSearchMetric(
        percentageSearchesLeadingToPlayback = 0.7,
        percentagePlaybacksTop3 = 0.4
      )
    )

    json.get("percentageSearchesLeadingToPlayback").getAsDouble shouldBe 0.7
    json.get("percentagePlaybacksTop3").getAsDouble shouldBe 0.4
  }

}
