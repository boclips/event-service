package com.boclips.event.aggregator.domain.service.search

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.service.session.SessionService
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideosSearchedEvent
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity

class SessionServiceTest extends Test {
  "separateEventsIntoSessions" should "put events in the same session when they are close in time" in {
    val events = List(
      createVideosSearchedEvent(timestamp = ZonedDateTime.now()),
      createVideosSearchedEvent(timestamp = ZonedDateTime.now()),
    )

    val sessions = {
      new SessionService().separateEventsIntoSessions(owner = createBoclipsUserIdentity(), events)
    }

    sessions should have size 1
    sessions.head.events should have size 2
  }

  it should "split events into separate sessions when not close in time" in {
    val events = List(
      createVideosSearchedEvent(timestamp = ZonedDateTime.now()),
      createVideosSearchedEvent(timestamp = ZonedDateTime.now()),
      createVideosSearchedEvent(timestamp = ZonedDateTime.now().minusMonths(1)),
      createVideosSearchedEvent(timestamp = ZonedDateTime.now().minusMonths(1)),
    )

    val sessions = new SessionService().separateEventsIntoSessions(owner = createBoclipsUserIdentity(), events)

    sessions should have size 2
    sessions.head.events should have size 2
    sessions.tail.head.events should have size 2
  }
}
