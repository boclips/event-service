package com.boclips.event.aggregator.domain.service.playback

import java.time.Duration

import com.boclips.event.aggregator.domain.model.Url
import com.boclips.event.aggregator.domain.model.users.{DeviceId, UserId}
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoSegmentPlayedEvent
import com.boclips.event.aggregator.testsupport.testfactories.SessionFactory.createSession
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createAnonymousUserIdentity

//noinspection ZeroIndexToHead
class SessionPlaybackAssemblerTest extends Test {

  "aggregate playback events" should "sum watched seconds by video id" in {
    val session = createSession(
      user = UserFactory.createBoclipsUserIdentity(userId = "user"),
      events = List(
        createVideoSegmentPlayedEvent(videoId = "v1", secondsWatched = 20),
        createVideoSegmentPlayedEvent(videoId = "v1", secondsWatched = 30),
      ))

    val highLevelEvents = new SessionPlaybackAssembler().assemblePlaybacksInSession(session, Map())

    highLevelEvents should have size 1
    highLevelEvents.head.videoId shouldBe VideoId("v1")
    highLevelEvents.head.user.id should contain(UserId("user"))
    highLevelEvents.head.secondsWatched shouldBe 50
  }

  it should "only aggregate seconds watched when video id is the same" in {
    val session = createSession(events = List(
      createVideoSegmentPlayedEvent(videoId = "v1", secondsWatched = 20),
      createVideoSegmentPlayedEvent(videoId = "v1", secondsWatched = 30),
      createVideoSegmentPlayedEvent(videoId = "v2", secondsWatched = 30),
    ))

    val highLevelEvents = new SessionPlaybackAssembler().assemblePlaybacksInSession(session, Map()).toList.sortBy(e => e.videoId)

    highLevelEvents should have size 2
    highLevelEvents(0).videoId shouldBe VideoId("v1")
    highLevelEvents(1).videoId shouldBe VideoId("v2")
    highLevelEvents(0).secondsWatched shouldBe 50
    highLevelEvents(1).secondsWatched shouldBe 30
  }

  it should "include url path of one of the segment events" in {
    val session = createSession(events = List(
      createVideoSegmentPlayedEvent(videoId = "v1", url = Url.parse("http://example.com/a")),
      createVideoSegmentPlayedEvent(videoId = "v1", url = Url.parse("http://example.com/b")),
    ))

    val highLevelEvents = new SessionPlaybackAssembler().assemblePlaybacksInSession(session, Map()).toList

    List("/a", "/b") should contain(highLevelEvents.map(_.url.get.path).head)
  }

  it should "include referer id when present on any segment played event" in {
    val session = createSession(events = List(
      createVideoSegmentPlayedEvent(videoId = "v1", refererId = None),
      createVideoSegmentPlayedEvent(videoId = "v1", refererId = Some("refererId")),
    ))

    val highLevelEvents = new SessionPlaybackAssembler().assemblePlaybacksInSession(session, Map())

    highLevelEvents should have size 1
    highLevelEvents.head.refererId shouldBe Some(UserId("refererId"))
  }

  it should "use id of the first segment played event as playback id" in {
    val session = createSession(events = List(
      createVideoSegmentPlayedEvent(videoId = "v1", id = "first-id"),
      createVideoSegmentPlayedEvent(videoId = "v1", id = "second-id"),
    ))

    val playbacks = new SessionPlaybackAssembler().assemblePlaybacksInSession(session, Map())

    playbacks should have size 1
    playbacks.head.id shouldBe "first-id"
  }

  it should "use device id of the first segment played event" in {
    val session = createSession(events = List(
      createVideoSegmentPlayedEvent(videoId = "v1", userIdentity = createAnonymousUserIdentity(Some("device id"))),
      createVideoSegmentPlayedEvent(videoId = "v1"),
    ))

    val playbacks = new SessionPlaybackAssembler().assemblePlaybacksInSession(session, Map())

    playbacks should have size 1
    playbacks.head.deviceId shouldBe Some(DeviceId("device id"))
  }

  it should "set video duration" in {
    val session = createSession(events = List(
      createVideoSegmentPlayedEvent(videoId = "v1"),
    ))

    val durationSecondsByVideoId = Map("v1" -> 90)

    val playbacks = new SessionPlaybackAssembler() assemblePlaybacksInSession(session, durationSecondsByVideoId)

    playbacks should have size 1
    playbacks.head.videoDuration shouldBe Duration.ofSeconds(90)
  }

}
