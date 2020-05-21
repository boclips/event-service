package com.boclips.event.aggregator.domain.model

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoSegmentPlayedEvent
import com.boclips.event.aggregator.testsupport.testfactories.SessionFactory.createSession

class SessionTest extends Test {

  "start" should "be the time of the first event" in {
    val session = createSession(events = List[Event](
      createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.parse("2020-02-10T10:11:12Z")),
      createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.parse("2020-02-10T10:11:10Z")),
      createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.parse("2020-02-10T10:11:11Z")),
    ))

    session.start shouldBe ZonedDateTime.parse("2020-02-10T10:11:10Z")
  }

  "end" should "be the time of the last event" in {
    val session = createSession(events = List[Event](
      createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.parse("2020-02-10T10:11:12Z")),
      createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.parse("2020-02-10T10:11:10Z")),
      createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.parse("2020-02-10T10:11:11Z")),
    ))

    session.end shouldBe ZonedDateTime.parse("2020-02-10T10:11:12Z")
  }

}
