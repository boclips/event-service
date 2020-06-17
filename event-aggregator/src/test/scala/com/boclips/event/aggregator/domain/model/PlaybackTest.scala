package com.boclips.event.aggregator.domain.model

import java.time.Duration

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.PlaybackFactory.createPlayback
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.{createBoclipsUserIdentity, createUser}

class PlaybackTest extends Test {

  "isShare" should "be true when referer id and user id are different" in {
    val playback = createPlayback(
      user = createBoclipsUserIdentity(userId = "user"),
      refererId = Some("referer")
    )

    playback.isShare shouldBe true
  }

  it should "be false when there is no referer" in {
    val playback = createPlayback(
      user = createBoclipsUserIdentity(userId = "user"),
      refererId = None
    )

    playback.isShare shouldBe false
  }

  it should "be false when referer is the same as user" in {
    val playback = createPlayback(
      user = createBoclipsUserIdentity(userId = "user"),
      refererId = Some("user")
    )

    playback.isShare shouldBe false
  }

  it should "be true when referer is not known but user is anonymous" in {
    val playback = createPlayback(
      user = AnonymousUserIdentity(deviceId = Some(DeviceId("device-id"))),
      refererId = None
    )

    playback.isShare shouldBe true
  }

  "payable" should "be set when at least 20% have been played (videos up to 60s)" in {
    createPlayback(secondsWatched = 1, videoDuration = Duration.ofSeconds(10)).isPayable shouldBe false
    createPlayback(secondsWatched = 2, videoDuration = Duration.ofSeconds(10)).isPayable shouldBe true
  }

  it should "be set when at least 30s have been played (videos over 60s)" in {
    createPlayback(secondsWatched = 12, videoDuration = Duration.ofSeconds(60)).isPayable shouldBe true
    createPlayback(secondsWatched = 12, videoDuration = Duration.ofSeconds(61)).isPayable shouldBe false
    createPlayback(secondsWatched = 29, videoDuration = Duration.ofSeconds(61)).isPayable shouldBe false
    createPlayback(secondsWatched = 30, videoDuration = Duration.ofSeconds(61)).isPayable shouldBe true
  }

  it should "be false when video has duration 0" in {
    createPlayback(secondsWatched = 100, videoDuration = Duration.ofSeconds(0)).isPayable shouldBe false
  }
}
