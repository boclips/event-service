package com.boclips.event.aggregator.infrastructure.bigquery

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.testsupport.Test

class TempFolderNameTest extends Test {

  it should "create names based on time" in {
    val time = ZonedDateTime.of(2019, 12, 24, 10, 11, 12, 134500000, ZoneOffset.UTC)

    TempFolderName(time) shouldBe "2019-12-24_10-11-12.134"
  }
}
