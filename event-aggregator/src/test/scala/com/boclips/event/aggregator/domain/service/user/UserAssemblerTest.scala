package com.boclips.event.aggregator.domain.service.user

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model.users._
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoSegmentPlayedEvent
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createUser

class UserAssemblerTest extends IntegrationTest {

  it should "keep users with Boclips ids when no external ids in events" in sparkTest { implicit spark =>
    val users = UserAssembler(
      users = rdd(
        createUser(identity = BoclipsUserIdentity(UserId("user1")))
      ),
      events = rdd[Event]()
    ).collect().toList

    users should have size 1
    users.head.identity shouldBe BoclipsUserIdentity(UserId("user1"))
  }

  it should "create user clones for each external user id" in sparkTest { implicit spark =>
    val users = UserAssembler(
      users = rdd(
        createUser(identity = BoclipsUserIdentity(UserId("user1")))
      ),
      events = rdd(
        createVideoSegmentPlayedEvent(userIdentity = ExternalUserIdentity(UserId("user1"), ExternalUserId("e1"))),
        createVideoSegmentPlayedEvent(userIdentity = ExternalUserIdentity(UserId("user1"), ExternalUserId("e2"))),
      )
    ).collect().toList

    users should have size 3
    users.map(_.identity) should contain(BoclipsUserIdentity(UserId("user1")))
    users.map(_.identity) should contain(ExternalUserIdentity(UserId("user1"), ExternalUserId("e1")))
    users.map(_.identity) should contain(ExternalUserIdentity(UserId("user1"), ExternalUserId("e2")))
  }

  it should "create exactly one user clone for each external user id" in sparkTest { implicit spark =>
    val users = UserAssembler(
      users = rdd(
        createUser(identity = BoclipsUserIdentity(UserId("user1")))
      ),
      events = rdd(
        createVideoSegmentPlayedEvent(userIdentity = ExternalUserIdentity(UserId("user1"), ExternalUserId("e1"))),
        createVideoSegmentPlayedEvent(userIdentity = ExternalUserIdentity(UserId("user1"), ExternalUserId("e1"))),
      )
    ).collect().toList

    users should have size 2
    users.map(_.identity) should contain(BoclipsUserIdentity(UserId("user1")))
    users.map(_.identity) should contain(ExternalUserIdentity(UserId("user1"), ExternalUserId("e1")))
  }

  it should "create user objects for anonymous identities with device id" in sparkTest { implicit spark =>
    val identity = AnonymousUserIdentity(Some(DeviceId("123")))
    val now = ZonedDateTime.now()
    val users = UserAssembler(
      users = rdd[User](),
      events = rdd(
        createVideoSegmentPlayedEvent(userIdentity = identity, timestamp = now.plusDays(1)),
        createVideoSegmentPlayedEvent(userIdentity = identity, timestamp = now),
        createVideoSegmentPlayedEvent(userIdentity = identity, timestamp = now.plusDays(1)),
      )
    ).collect().toList

    users should have size 1
    users.head.identity shouldBe identity
    users.head.createdAt shouldBe now
  }

  it should "not create user objects for anonymous identities without device id" in sparkTest { implicit spark =>
    val users = UserAssembler(
      users = rdd[User](),
      events = rdd(
        createVideoSegmentPlayedEvent(userIdentity = AnonymousUserIdentity(None)),
      )
    ).collect().toList

    users should have size 0
  }
}
