package com.boclips.event.aggregator.presentation.formatters

import java.time.{Duration, ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.Url
import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.users.User
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.PlaybackFactory.createPlayback
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.{createBoclipsUserIdentity, createUser}

class PlaybackFormatterTest extends Test {

  implicit class PlaybackExtensions(val playback: Playback) {
    def withUser(
                  user: Option[User] = None,
                ): (Playback, Option[User]) = (playback, user)

  }

  implicit def playback2playbackWithRelatedData(playback: Playback): (Playback, Option[User]) = playback.withUser()

  it should "write playback id" in {
    val json = PlaybackFormatter.formatRow(createPlayback(id = "playback-id"))

    json.getString("id") shouldBe "playback-id"
  }

  it should "write user id when user is not anonymous" in {
    val json = PlaybackFormatter.formatRow(createPlayback(user = createBoclipsUserIdentity("user-x")))

    json.getString("userId") shouldBe "user-x"
  }

  it should "write refererId when it is present" in {
    val json = PlaybackFormatter.formatRow(createPlayback(refererId = Some("refererId")))

    json.getString("refererId") shouldBe "refererId"
  }

  it should "write null when refererId is not present" in {
    val json = PlaybackFormatter.formatRow(createPlayback(refererId = None))

    json.get("refererId").isJsonNull shouldBe true
  }

  it should "write timestamp" in {
    val json = PlaybackFormatter.formatRow(createPlayback(timestamp = ZonedDateTime.of(2017, 3, 23, 18, 25, 41, 511000000, ZoneOffset.UTC)))

    json.getString("timestamp") shouldBe "2017-03-23T18:25:41.511Z"
  }

  it should "write video id" in {
    val json = PlaybackFormatter.formatRow(createPlayback(videoId = "video-id"))

    json.getString("videoId") shouldBe "video-id"
  }

  it should "write seconds watched" in {
    val json = PlaybackFormatter.formatRow(createPlayback(secondsWatched = 50))

    json.get("secondsWatched").getAsInt shouldBe 50
  }

  it should "write video duration" in {
    val json = PlaybackFormatter.formatRow(createPlayback(videoDuration = Duration.ofSeconds(180)))

    json.get("durationSeconds").getAsInt shouldBe 180
  }

  it should "write a different playback id for different events" in {
    val playbackEvent1 = createPlayback()
    val playbackEvent2 = createPlayback()

    val json1 = PlaybackFormatter.formatRow(playbackEvent1)
    val json2 = PlaybackFormatter.formatRow(playbackEvent2)

    json1.getString("playbackId") should not be json2.getString("playbackId")
  }

  it should "write the url" in {
    val playbackEvent1 = createPlayback(url = Url.parse("http://example.com/path/to"))

    val json = PlaybackFormatter.formatRow(playbackEvent1)

    json.getString("urlHost") shouldBe "example.com"
    json.getString("urlPath") shouldBe "/path/to"
  }

  it should "write the share flag" in {
    val event = createPlayback()

    val json = PlaybackFormatter.formatRow(event)

    json.getBool("isShare") shouldBe false
  }

  it should "write pct of video duration watched" in {
    val json = PlaybackFormatter.formatRow(createPlayback(secondsWatched = 120, videoDuration = Duration.ofMinutes(3)))
    json.get("percentageOfVideoDurationWatched").getAsDouble shouldBe 0.66 +- 0.01

  }
  it should "write pct of video duration zero when duration is zero" in {
    val json = PlaybackFormatter.formatRow(createPlayback(secondsWatched = 120, videoDuration = Duration.ofSeconds(0)))
    json.get("percentageOfVideoDurationWatched").getAsDouble shouldBe 0
  }

  it should "write payable flag when playback is payable" in {
    val json = PlaybackFormatter.formatRow(createPlayback(secondsWatched = 120, videoDuration = Duration.ofMinutes(3)))
    json.getBool("isPayable") shouldBe true
  }

  it should "write payable flag when playback is not payable" in {
    val json = PlaybackFormatter.formatRow(createPlayback(secondsWatched = 20, videoDuration = Duration.ofMinutes(6)))
    json.getBool("isPayable") shouldBe false
  }

  it should "write payable flag when there is no video" in {
    val json = PlaybackFormatter.formatRow(createPlayback(secondsWatched = 20, videoDuration = Duration.ofMinutes(6)))
    json.getBool("isPayable") shouldBe false
  }

  it should "write device id when present" in {
    val json = PlaybackFormatter.formatRow(createPlayback(deviceId = Some("device-id")))

    json.getString("deviceId") shouldBe "device-id"
  }

  it should "write device id as null when not present" in {
    val json = PlaybackFormatter.formatRow(createPlayback(deviceId = None))

    json.getString("deviceId") shouldBe "UNKNOWN"
  }

  it should "write user when not anonymous" in {
    val json = PlaybackFormatter.formatRow(createPlayback().withUser(user = Some(createUser(createBoclipsUserIdentity("user-id")))))

    json.getAsJsonObject("user").getString("id") shouldBe "user-id"
  }

  it should "write user as null when anonymous" in {
    val json = PlaybackFormatter.formatRow(createPlayback().withUser(user = None))

    json.get("user").isJsonNull shouldBe true
  }
}
