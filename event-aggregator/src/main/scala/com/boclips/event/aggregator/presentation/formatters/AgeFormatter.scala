package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.AgeRange

object AgeFormatter {

  def formatAges(ageRange: AgeRange): List[String] = ageRange match {
    case AgeRange(Some(min), max) => (min to max.getOrElse(19)).map(age => f"$age%02d").toList
    case AgeRange(None, _) => List("UNKNOWN")
  }

}
