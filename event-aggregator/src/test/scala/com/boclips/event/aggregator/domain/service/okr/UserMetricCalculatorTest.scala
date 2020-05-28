package com.boclips.event.aggregator.domain.service.okr

import java.time.Month._

import com.boclips.event.aggregator.domain.model.events.VideosSearchedEvent
import com.boclips.event.aggregator.domain.model.{Monthly, User}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.TestTimestamps.{oneDayThis, thisYearIn, thisYearWhole}
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createUser
import org.apache.spark.rdd.RDD

class UserMetricCalculatorTest extends IntegrationTest {

  "calculateStats" should "count new and repeat users" in sparkTest { implicit spark =>
    implicit val events: RDD[VideosSearchedEvent] = rdd(
      EventFactory.createVideosSearchedEvent(userId = "aly", timestamp = thisYearIn(MAY)),
      EventFactory.createVideosSearchedEvent(userId = "aly", timestamp = thisYearIn(MAY)),
      EventFactory.createVideosSearchedEvent(userId = "bob", timestamp = thisYearIn(MAY)),

      EventFactory.createVideosSearchedEvent(userId = "bob", timestamp = thisYearIn(JUNE)),
      EventFactory.createVideosSearchedEvent(userId = "cal", timestamp = thisYearIn(JUNE)),
      EventFactory.createVideosSearchedEvent(userId = "don", timestamp = thisYearIn(JUNE)),
    )

    implicit val users: RDD[User] = rdd()

    val activeUsers = UserMetricCalculator calculateMetrics Monthly()

    activeUsers.find(_.timePeriod == thisYearWhole(MAY)).get.activeUserCounts.newUsers shouldBe 2
    activeUsers.find(_.timePeriod == thisYearWhole(MAY)).get.activeUserCounts.repeatUsers shouldBe 0
    activeUsers.find(_.timePeriod == thisYearWhole(JUNE)).get.activeUserCounts.newUsers shouldBe 2
    activeUsers.find(_.timePeriod == thisYearWhole(JUNE)).get.activeUserCounts.repeatUsers shouldBe 1
  }

  it should "calculate recently active users at the beginning and end of a time period" in sparkTest { implicit spark =>
    implicit val events: RDD[VideosSearchedEvent] = rdd(
      EventFactory.createVideosSearchedEvent(userId = "aly", timestamp = thisYearIn(MAY).withDayOfMonth(1)),
      EventFactory.createVideosSearchedEvent(userId = "ben", timestamp = thisYearIn(MAY).withDayOfMonth(30)),
      EventFactory.createVideosSearchedEvent(userId = "cal", timestamp = thisYearIn(JUNE)),
      EventFactory.createVideosSearchedEvent(userId = "cal", timestamp = thisYearIn(JULY))
    )

    implicit val users: RDD[User] = rdd()

    val userMetrics = UserMetricCalculator calculateMetrics Monthly()

    userMetrics.find(_.timePeriod == thisYearWhole(JUNE)).get.recentlyActiveAtStart shouldBe 2
    userMetrics.find(_.timePeriod == thisYearWhole(JUNE)).get.recentlyActiveAtEnd shouldBe 2
  }

  it should "calculate total accounts and total activated accounts at the beginning and end of a period" in sparkTest { implicit spark =>
    implicit val events: RDD[VideosSearchedEvent] = rdd(
      EventFactory.createVideosSearchedEvent(timestamp = thisYearIn(MAY)),
      EventFactory.createVideosSearchedEvent(timestamp = thisYearIn(JUNE)),
      EventFactory.createVideosSearchedEvent(timestamp = thisYearIn(JULY)),
    )

    implicit val users: RDD[User] = rdd(
      createUser(id = "aly", createdAt = thisYearIn(MAY)),
      createUser(id = "ben", createdAt = thisYearIn(MAY)),
      createUser(id = "cal", createdAt = thisYearIn(JUNE)),
      createUser(id = "dom", createdAt = thisYearIn(JUNE)),
      createUser(id = "eve", createdAt = thisYearIn(JUNE)),
      createUser(id = "fad", createdAt = thisYearIn(JULY)),
    )

    val userMetrics = UserMetricCalculator calculateMetrics Monthly()

    userMetrics.find(_.timePeriod == thisYearWhole(MAY)).get.totalsAtStart.totalAccounts shouldBe 0
    userMetrics.find(_.timePeriod == thisYearWhole(MAY)).get.totalsAtEnd.totalAccounts shouldBe 2
    userMetrics.find(_.timePeriod == thisYearWhole(JUNE)).get.totalsAtStart.totalAccounts shouldBe 2
    userMetrics.find(_.timePeriod == thisYearWhole(JUNE)).get.totalsAtEnd.totalAccounts shouldBe 5
    userMetrics.find(_.timePeriod == thisYearWhole(JULY)).get.totalsAtStart.totalAccounts shouldBe 5
    userMetrics.find(_.timePeriod == thisYearWhole(JULY)).get.totalsAtEnd.totalAccounts shouldBe 6
  }

  "calculateNumberNonChurnedUsers" should "calculateNumberNonChurnedUsers" in sparkTest { implicit spark =>
    implicit val events: RDD[VideosSearchedEvent] = rdd(
      EventFactory.createVideosSearchedEvent(userId = "aly", timestamp = thisYearIn(JANUARY)),
      EventFactory.createVideosSearchedEvent(userId = "aly", timestamp = thisYearIn(DECEMBER)),
      EventFactory.createVideosSearchedEvent(userId = "ben", timestamp = thisYearIn(MAY)),
      EventFactory.createVideosSearchedEvent(userId = "cal", timestamp = thisYearIn(DECEMBER))
    )

    implicit val users: RDD[User] = rdd()

    val nonChurnedUsers = UserMetricCalculator countActiveUsers(fromInclusive = oneDayThis(MARCH), toExclusive = oneDayThis(JUNE))

    nonChurnedUsers shouldBe 1
  }
}
