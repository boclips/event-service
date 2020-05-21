package com.boclips.event.aggregator.infrastructure.bigquery

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TempFolderName {

  def apply(time: ZonedDateTime = ZonedDateTime.now()): String = {
    time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss.SSS"))
  }
}
