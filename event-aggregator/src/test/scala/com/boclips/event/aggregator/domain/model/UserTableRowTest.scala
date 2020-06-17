package com.boclips.event.aggregator.domain.model

import java.time.Month.{FEBRUARY, JANUARY, NOVEMBER}
import java.time.{LocalDate, YearMonth, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.users.UserActiveStatus
import com.boclips.event.aggregator.presentation.model.UserTableRow
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory

class UserTableRowTest extends Test {

  val today: LocalDate = LocalDate.parse("2020-02-11")

  it should "mark user active in the month of activity" in {

    val user = UserFactory.createUser(createdAt = ZonedDateTime.parse("2020-02-10T00:00:00Z"))

    val userWithStatus = UserTableRow.from(user, playbacks = List(), referredPlaybacks = List(), searches = List(), sessions = List(), monthsActive = List(YearMonth.of(2020, FEBRUARY)), today)

    userWithStatus.monthlyActiveStatus should have size 1
    userWithStatus.monthlyActiveStatus.head.month shouldBe YearMonth.of(2020, FEBRUARY)
    userWithStatus.monthlyActiveStatus.head.isActive shouldBe true
  }

  it should "create active status entries for each month since signup until the current month" in {

    val user = UserFactory.createUser(createdAt = ZonedDateTime.parse("2020-01-10T00:00:00Z"))

    val userWithStatus = UserTableRow.from(user, playbacks = List(), referredPlaybacks = List(), searches = List(), sessions = List(), monthsActive = List(YearMonth.of(2020, JANUARY)), today)

    userWithStatus.monthlyActiveStatus should have size 2
    userWithStatus.monthlyActiveStatus should contain(UserActiveStatus(month = YearMonth.of(2020, JANUARY), isActive = true))
    userWithStatus.monthlyActiveStatus should contain(UserActiveStatus(month = YearMonth.of(2020, FEBRUARY), isActive = true))
  }

  it should "mark user as not active when there have been over 60 days since playback" in {

    val user = UserFactory.createUser(createdAt = ZonedDateTime.parse("2019-11-01T00:00:00Z"))

    val userWithStatus = UserTableRow.from(user, playbacks = List(), referredPlaybacks = List(), searches = List(), sessions = List(), monthsActive = List(YearMonth.of(2019, NOVEMBER)), today)

    userWithStatus.monthlyActiveStatus should have size 4
    userWithStatus.monthlyActiveStatus should contain(UserActiveStatus(month = YearMonth.of(2020, JANUARY), isActive = true))
    userWithStatus.monthlyActiveStatus should contain(UserActiveStatus(month = YearMonth.of(2020, FEBRUARY), isActive = false))
  }

}
