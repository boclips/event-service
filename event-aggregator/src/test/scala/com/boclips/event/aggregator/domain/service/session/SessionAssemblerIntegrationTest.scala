package com.boclips.event.aggregator.domain.service.session

import com.boclips.event.aggregator.domain.model.{DeviceId, User, UserId}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.{createVideoSegmentPlayedEvent, createVideosSearchedEvent}
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.{createAnonymousUserIdentity, createBoclipsUserIdentity, createUser}

class SessionAssemblerIntegrationTest extends IntegrationTest {

  it should "match users with their sessions" in sparkTest { implicit spark =>
    val users = rdd(
      createUser(id = "user-1"),
    )

    val events = rdd(
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("user-1")),
      createVideoSegmentPlayedEvent(userIdentity = createAnonymousUserIdentity(deviceId = Some("device-1"))),
      createVideoSegmentPlayedEvent(userIdentity = createBoclipsUserIdentity("user-1")),
    )

    val sessions = new SessionAssembler(events, users, "").assembleSessions().collect()
      .sortBy(_.events.length)
      .toList

    val anonymousUserSession :: loggedInUserSession :: _ = sessions

    anonymousUserSession.user.asAnonymous.deviceId shouldBe Some(DeviceId("device-1"))
    anonymousUserSession.events should have length 1

    loggedInUserSession.user.asUser.id shouldBe UserId("user-1")
    loggedInUserSession.events should have length 2
  }

  it should "gracefully handle cases when user id is there but the user object does not exist" in sparkTest { implicit spark =>
    val users = rdd[User]()

    val events = rdd(
      createVideosSearchedEvent(userIdentity = createBoclipsUserIdentity("user-1")),
    )

    val session = new SessionAssembler(events, users, "").assembleSessions().collect().head

    session.user.asAnonymous.deviceId shouldBe Some(DeviceId("user-1"))
  }

}
