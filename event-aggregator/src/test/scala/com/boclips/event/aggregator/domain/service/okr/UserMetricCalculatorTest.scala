package com.boclips.event.aggregator.domain.service.okr

import java.time.Month._

import com.boclips.event.aggregator.domain.model.events.VideosSearchedEvent
import com.boclips.event.aggregator.domain.model.okrs.Monthly
import com.boclips.event.aggregator.domain.model.users.User
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.TestTimestamps.{oneDayThis, thisYearIn, thisYearWhole}
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideosSearchedEvent
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.{createBoclipsUserIdentity, createUser}
import org.apache.spark.rdd.RDD

class UserMetricCalculatorTest extends IntegrationTest {

  "calculateStats" should "count new and repeat users" in sparkTest { implicit spark =>
    implicit val events: RDD[VideosSearchedEvent] = rdd(
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("aly"), timestamp = thisYearIn(MAY)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("aly"), timestamp = thisYearIn(MAY)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("bob"), timestamp = thisYearIn(MAY)),

      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("bob"), timestamp = thisYearIn(JUNE)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("cal"), timestamp = thisYearIn(JUNE)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("don"), timestamp = thisYearIn(JUNE)),
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
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("aly"), timestamp = thisYearIn(MAY).withDayOfMonth(1)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("ben"), timestamp = thisYearIn(MAY).withDayOfMonth(30)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("cal"), timestamp = thisYearIn(JUNE)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("cal"), timestamp = thisYearIn(JULY))
    )

    implicit val users: RDD[User] = rdd()

    val userMetrics = UserMetricCalculator calculateMetrics Monthly()

    userMetrics.find(_.timePeriod == thisYearWhole(JUNE)).get.recentlyActiveAtStart shouldBe 2
    userMetrics.find(_.timePeriod == thisYearWhole(JUNE)).get.recentlyActiveAtEnd shouldBe 2
  }

  it should "calculate total accounts and total activated accounts at the beginning and end of a period" in sparkTest { implicit spark =>
    implicit val events: RDD[VideosSearchedEvent] = rdd(
      createVideosSearchedEvent(timestamp = thisYearIn(MAY)),
      createVideosSearchedEvent(timestamp = thisYearIn(JUNE)),
      createVideosSearchedEvent(timestamp = thisYearIn(JULY)),
    )

    implicit val users: RDD[User] = rdd(
      createUser(identity = createBoclipsUserIdentity("aly"), createdAt = thisYearIn(MAY)),
      createUser(identity = createBoclipsUserIdentity("ben"), createdAt = thisYearIn(MAY)),
      createUser(identity = createBoclipsUserIdentity("cal"), createdAt = thisYearIn(JUNE)),
      createUser(identity = createBoclipsUserIdentity("dom"), createdAt = thisYearIn(JUNE)),
      createUser(identity = createBoclipsUserIdentity("eve"), createdAt = thisYearIn(JUNE)),
      createUser(identity = createBoclipsUserIdentity("fad"), createdAt = thisYearIn(JULY)),
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
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("aly"), timestamp = thisYearIn(JANUARY)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("aly"), timestamp = thisYearIn(DECEMBER)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("ben"), timestamp = thisYearIn(MAY)),
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("cal"), timestamp = thisYearIn(DECEMBER))
    )

    implicit val users: RDD[User] = rdd()

    val nonChurnedUsers = UserMetricCalculator countActiveUsers(fromInclusive = oneDayThis(MARCH), toExclusive = oneDayThis(JUNE))

    nonChurnedUsers shouldBe 1
  }
}
