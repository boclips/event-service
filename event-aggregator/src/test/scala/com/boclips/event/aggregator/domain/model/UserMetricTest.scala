package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.domain.model.okrs.ActiveUserCounts
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.MetricsFactory

class UserMetricTest extends Test {

  "repeatRate" should "calculate the correct ratio" in {
    val userStats = MetricsFactory.createUserMetric(activeUserCounts = ActiveUserCounts(newUsers = 30, repeatUsers = 70))

    userStats.repeatRate shouldBe 0.7
  }

  "churnRate" should "calculate the correct ratio" in {
    val userStats = MetricsFactory.createUserMetric(
      activeUserCounts = ActiveUserCounts(newUsers = 500, repeatUsers = 0),
      nonChurnedAtStart = 1000,
      nonChurnedAtEnd = 1200
    )

    userStats.churnRate shouldBe 0.3
  }

  "newAccounts" should "calculate the correct difference" in {
    val userStats = MetricsFactory.createUserMetric(
      totalsAtStart = MetricsFactory.createUserTotals(totalAccounts = 100),
      totalsAtEnd = MetricsFactory.createUserTotals(totalAccounts = 200)
    )

    userStats.newAccounts shouldBe 100
  }
}
