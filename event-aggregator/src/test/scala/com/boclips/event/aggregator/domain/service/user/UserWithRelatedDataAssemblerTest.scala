package com.boclips.event.aggregator.domain.service.user

import java.time.{YearMonth, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoSegmentPlayedEvent
import com.boclips.event.aggregator.testsupport.testfactories.PlaybackFactory.createPlayback
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createSearch, createSearchRequest}
import com.boclips.event.aggregator.testsupport.testfactories.SessionFactory.createSession
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.{createAnonymousUserIdentity, createUser}

class UserWithRelatedDataAssemblerTest extends IntegrationTest {

  val today: ZonedDateTime = ZonedDateTime.now()

  val user: User = createUser(createdAt = today, id = "user-1")

  val userIdentity: UserIdentity = BoclipsUserIdentity(UserId("user-1"))

  it should "mark a user active in the month of playback" in sparkTest { implicit spark =>
    val users = rdd(user)
    val playbacks = rdd(createPlayback(timestamp = today, user = userIdentity))
    val searches = rdd[Search]()
    val sessions = rdd[Session]()

    val usersWithRelatedData = UserWithRelatedDataAssembler(users, playbacks, searches, sessions).collect().toList

    usersWithRelatedData should have size 1
    usersWithRelatedData.head.monthlyActiveStatus should have size 1
    usersWithRelatedData.head.monthlyActiveStatus.head.month shouldBe YearMonth.of(today.getYear, today.getMonth)
    usersWithRelatedData.head.monthlyActiveStatus.head.isActive shouldBe true
  }
  it should "assemble all playbacks for one user" in sparkTest { implicit spark =>
    val users = rdd(user)
    val playbacks = rdd(createPlayback(timestamp = today, user = userIdentity))
    val searches = rdd[Search]()
    val sessions = rdd[Session]()

    val usersWithRelatedData = UserWithRelatedDataAssembler(users, playbacks, searches, sessions).collect().toList

    usersWithRelatedData should have size 1
    usersWithRelatedData.head.playbacks should have size 1
  }

  it should "assemble all referred playbacks for one user" in sparkTest { implicit spark =>
    val users = rdd(user)
    val playbacks = rdd(createPlayback(timestamp = today, user = createAnonymousUserIdentity(), refererId = Some(user.id.value)))
    val searches = rdd[Search]()
    val sessions = rdd[Session]()

    val usersWithRelatedData = UserWithRelatedDataAssembler(users, playbacks, searches, sessions).collect().toList

    usersWithRelatedData should have size 1
    usersWithRelatedData.head.referredPlaybacks should have size 1
  }

  it should "exclude self-referred playbacks" in sparkTest { implicit spark =>
    val users = rdd(user)
    val playbacks = rdd(createPlayback(timestamp = today, user = userIdentity, refererId = Some(user.id.value)))
    val searches = rdd[Search]()
    val sessions = rdd[Session]()

    val usersWithRelatedData = UserWithRelatedDataAssembler(users, playbacks, searches, sessions).collect().toList

    usersWithRelatedData should have size 1
    usersWithRelatedData.head.referredPlaybacks should have size 0
  }

  it should "assemble all searches for one user" in sparkTest { implicit spark =>
    val users = rdd(user)
    val playbacks = rdd[Playback]()
    val searches = rdd(createSearch(request = createSearchRequest(timestamp = today, userId = user.id.value)))
    val sessions = rdd[Session]()

    val usersWithRelatedData = UserWithRelatedDataAssembler(users, playbacks, searches, sessions).collect().toList

    usersWithRelatedData should have size 1
    usersWithRelatedData.head.searches should have size 1
  }

  it should "assemble all sessions for one user" in sparkTest { implicit spark =>
    val users = rdd(user)
    val playbacks = rdd[Playback]()
    val searches = rdd[Search]()
    val sessions = rdd(
      createSession(user = userIdentity, events = List(createVideoSegmentPlayedEvent())),
      createSession(user = createAnonymousUserIdentity(), events = List(createVideoSegmentPlayedEvent())),
    )

    val usersWithRelatedData = UserWithRelatedDataAssembler(users, playbacks, searches, sessions).collect().toList

    usersWithRelatedData should have size 1
    usersWithRelatedData.head.sessions should have size 1
  }

}
