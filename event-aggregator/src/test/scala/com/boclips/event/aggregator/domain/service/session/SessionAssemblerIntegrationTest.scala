package com.boclips.event.aggregator.domain.service.session

import com.boclips.event.aggregator.domain.model.users.{DeviceId, UserId}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.{createVideoSegmentPlayedEvent, createVideosSearchedEvent}
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.{createAnonymousUserIdentity, createBoclipsUserIdentity, createUser}

class SessionAssemblerIntegrationTest extends IntegrationTest {

  it should "match users with their sessions" in sparkTest { implicit spark =>
    val events = rdd(
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("user-1")),
      createVideoSegmentPlayedEvent(userIdentity = createAnonymousUserIdentity(deviceId = Some("device-1"))),
      createVideoSegmentPlayedEvent(userIdentity = createBoclipsUserIdentity("user-1")),
    )

    val sessions = new SessionAssembler(events, "").assembleSessions().collect()
      .sortBy(_.events.length)
      .toList

    val anonymousUserSession :: loggedInUserSession :: _ = sessions

    anonymousUserSession.user.deviceId should contain (DeviceId("device-1"))
    anonymousUserSession.events should have length 1

    loggedInUserSession.user.id should contain (UserId("user-1"))
    loggedInUserSession.events should have length 2
  }

}
