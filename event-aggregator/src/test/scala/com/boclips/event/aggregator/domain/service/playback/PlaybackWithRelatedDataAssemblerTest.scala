package com.boclips.event.aggregator.domain.service.playback

import com.boclips.event.aggregator.domain.model.{AnonymousUserIdentity, BoclipsUserIdentity, User, UserId}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.PlaybackFactory.createPlayback
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createUser

class PlaybackWithRelatedDataAssemblerTest extends IntegrationTest {

  it should "include users when exist" in sparkTest { implicit spark =>

    val userIdentity = BoclipsUserIdentity(UserId("user-id"))
    val playback = createPlayback(user = userIdentity)
    val user = createUser(identity = userIdentity)

    val playbacksWithRelatedData = new PlaybackWithRelatedDataAssembler(rdd(playback), rdd(user))
      .assemblePlaybacksWithRelatedData()
      .collect()
      .toList

    playbacksWithRelatedData should have length 1
    playbacksWithRelatedData.head.playback shouldBe playback
    playbacksWithRelatedData.head.user should contain(user)
  }

  it should "not include users for anonymous playbacks" in sparkTest { implicit spark =>
    val userIdentity = BoclipsUserIdentity(UserId("user-id"))
    val playback = createPlayback(user = AnonymousUserIdentity(deviceId = None))
    val user = createUser(identity = userIdentity)

    val playbacksWithRelatedData = new PlaybackWithRelatedDataAssembler(rdd(playback), rdd(user))
      .assemblePlaybacksWithRelatedData()
      .collect()
      .toList

    playbacksWithRelatedData should have length 1
    playbacksWithRelatedData.head.playback shouldBe playback
    playbacksWithRelatedData.head.user shouldBe empty
  }

  it should "not include users for no matches" in sparkTest { implicit spark =>
    val playback = createPlayback(user = AnonymousUserIdentity(deviceId = None))

    val playbacksWithRelatedData = new PlaybackWithRelatedDataAssembler(rdd(playback), rdd[User]())
      .assemblePlaybacksWithRelatedData()
      .collect()
      .toList

    playbacksWithRelatedData should have length 1
    playbacksWithRelatedData.head.playback shouldBe playback
    playbacksWithRelatedData.head.user shouldBe empty
  }

  it should "keep all playbacks by anonymous users" in sparkTest { implicit spark =>
    val playbacks = rdd(
      createPlayback(user = AnonymousUserIdentity(deviceId = None)),
      createPlayback(user = AnonymousUserIdentity(deviceId = None)),
    )
    val playbacksWithRelatedData = new PlaybackWithRelatedDataAssembler(playbacks, rdd[User]())
      .assemblePlaybacksWithRelatedData()
      .collect()
      .toList

    playbacksWithRelatedData should have size 2
  }

}
