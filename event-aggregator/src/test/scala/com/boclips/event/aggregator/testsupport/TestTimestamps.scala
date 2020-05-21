package com.boclips.event.aggregator.testsupport

import java.time.ZoneOffset.UTC
import java.time.{LocalDate, Month, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{DateRange, Monthly}

object TestTimestamps {

  def oneDayThis(month: Month): LocalDate = LocalDate.now(UTC).withMonth(month.getValue)

  def thisYearWhole(month: Month): DateRange = wholeMonthOf(thisYearIn(month))

  def thisYearIn(month: Month): ZonedDateTime = ZonedDateTime.now(UTC).withMonth(month.getValue)

  def wholeMonthOf(month: Month, year: Int): DateRange = wholeMonthOf(ZonedDateTime.of(year, month.getValue, 1, 0, 0, 0, 0, UTC))

  def wholeMonthOf(timestamp: ZonedDateTime): DateRange = Monthly() dateRangeOf timestamp
}
