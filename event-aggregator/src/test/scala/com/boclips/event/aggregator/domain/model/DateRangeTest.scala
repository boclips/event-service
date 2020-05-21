package com.boclips.event.aggregator.domain.model

import java.time.Month.{APRIL, MAY}
import java.time.{LocalDate, ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.testsupport.Test

class DateRangeTest extends Test {

  "Weekly dateRangeOf" should "convert timestamp to a date range" in {
    val thursdayAfternoon = ZonedDateTime of(2019, 4, 18, 19, 6, 19, 134, ZoneOffset.UTC)

    val week = Weekly() dateRangeOf thursdayAfternoon

    week.startInclusive shouldBe (LocalDate of(2019, APRIL, 15))
    week.endExclusive shouldBe (LocalDate of(2019, APRIL, 22))
  }

  "Monthly dateRangeOf" should "convert timestamp to a date range" in {
    val thursdayAfternoon = ZonedDateTime of(2019, 4, 18, 19, 6, 19, 134, ZoneOffset.UTC)

    val month = Monthly() dateRangeOf thursdayAfternoon

    month.startInclusive shouldBe (LocalDate of(2019, APRIL, 1))
    month.endExclusive shouldBe (LocalDate of(2019, MAY, 1))
  }
}
