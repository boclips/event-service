package com.boclips.event.aggregator.presentation.formatters

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity
import com.boclips.event.aggregator.testsupport.testfactories.{EventFactory, SessionFactory}

class SessionFormatterTest extends Test {

  it should "write id start and end" in {
    val json = SessionFormatter formatRow SessionFactory.createSession(createBoclipsUserIdentity("id-666"),
      List(
        EventFactory.createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.of(2017, 3, 23, 18, 25, 41, 511000000, ZoneOffset.UTC)),
        EventFactory.createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.of(2017, 3, 23, 18, 30, 41, 511000000, ZoneOffset.UTC)))
    )

    json.getString("start") shouldBe "2017-03-23T18:25:41.511Z"
    json.getString("end") shouldBe "2017-03-23T18:30:41.511Z"

  }
}
