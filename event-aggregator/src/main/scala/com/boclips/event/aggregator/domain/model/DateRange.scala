package com.boclips.event.aggregator.domain.model

import java.time.ZoneOffset.UTC
import java.time.{LocalDate, ZonedDateTime}

case class DateRange(startInclusive: LocalDate, endExclusive: LocalDate) extends Comparable[DateRange] {

  override def compareTo(o: DateRange): Int = startInclusive.compareTo(o.startInclusive)
}

trait TimePeriodDuration {
  def dateRangeOf(timestamp: ZonedDateTime): DateRange
}

case class Weekly() extends TimePeriodDuration {
  override def dateRangeOf(timestamp: ZonedDateTime): DateRange = {
    val start = weekStartOf(timestamp withZoneSameInstant UTC)
    val end = start.plusWeeks(1)
    DateRange(startInclusive = start, endExclusive = end)
  }

  private def weekStartOf(timestamp: ZonedDateTime): LocalDate = {
    val dayOfWeek = timestamp.getDayOfWeek
    timestamp
      .minusDays(dayOfWeek.getValue - 1)
      .toLocalDate
  }
}

case class Monthly() extends TimePeriodDuration {
  override def dateRangeOf(timestamp: ZonedDateTime): DateRange = {
    val start = monthStartOf(timestamp withZoneSameInstant UTC)
    val end = start.plusMonths(1)
    DateRange(startInclusive = start, endExclusive = end)
  }

  private def monthStartOf(timestamp: ZonedDateTime): LocalDate = {
    timestamp
      .withDayOfMonth(1)
      .toLocalDate
  }
}
