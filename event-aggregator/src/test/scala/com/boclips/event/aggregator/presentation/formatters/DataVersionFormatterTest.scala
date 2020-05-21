package com.boclips.event.aggregator.presentation.formatters

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.testsupport.Test

class DataVersionFormatterTest extends Test {
  it should "create json with date & time property" in {
    val json = DataVersionFormatter formatRow ZonedDateTime.of(2019, 11, 18, 1, 2, 3, 400000000, ZoneOffset.UTC)

    json.getString("version") shouldBe "2019-11-18T01:02:03.4Z"
  }
}
