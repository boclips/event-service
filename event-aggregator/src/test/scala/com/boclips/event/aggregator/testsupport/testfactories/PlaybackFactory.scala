package com.boclips.event.aggregator.testsupport.testfactories

import java.time.{Duration, ZoneOffset, ZonedDateTime}
import java.util.UUID

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.users.{DeviceId, UserId, UserIdentity}
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity

object PlaybackFactory {

  def createPlayback(
                      id: String = UUID.randomUUID().toString,
                      timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                      user: UserIdentity = createBoclipsUserIdentity(),
                      videoId: String = "videoId",
                      secondsWatched: Int = 10,
                      url: Url = Url.parse("http://example.com/"),
                      refererId: Option[String] = None,
                      deviceId: Option[String] = None,
                      videoDuration: Duration = Duration.ofMinutes(2),
                    ): Playback = {
    Playback(
      id = id,
      timestamp = timestamp,
      user = user,
      videoId = VideoId(videoId),
      secondsWatched = secondsWatched,
      url = Option(url),
      refererId = refererId.map(UserId),
      deviceId = deviceId.map(DeviceId),
      videoDuration = videoDuration,
    )
  }
}
