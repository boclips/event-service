package com.boclips.event.aggregator.domain.model

import java.time.{Duration, ZonedDateTime}

case class Playback
(
  id: String,
  timestamp: ZonedDateTime,
  user: UserOrAnonymous,
  videoId: VideoId,
  secondsWatched: Int,
  url: Option[Url],
  refererId: Option[UserId],
  deviceId: Option[DeviceId],
  videoDuration: Duration,
) {

  def isShare: Boolean = {
    (user, refererId) match {
      case (u: User, Some(referer)) => u.id != referer
      case (_: User, None) => false
      case (_: AnonymousUser, _) => true
    }
  }

  def isPayable: Boolean = {
    videoDuration.getSeconds.toInt match {
      case durationSeconds if durationSeconds <= 0 => false
      case durationSeconds if durationSeconds <= 60 => secondsWatched >= 0.2 * durationSeconds
      case _ => secondsWatched >= 30
    }
  }
}
