package com.boclips.event.aggregator.domain.model

import java.time.{Duration, ZonedDateTime}

case class Playback
(
  id: String,
  timestamp: ZonedDateTime,
  user: UserIdentity,
  videoId: VideoId,
  secondsWatched: Int,
  url: Option[Url],
  refererId: Option[UserId],
  deviceId: Option[DeviceId],
  videoDuration: Duration,
) {
  def isShare: Boolean = {
    (user.boclipsId, refererId) match {
      case (Some(userId), Some(referer)) => userId != referer
      case (Some(_), None) => false
      case (None, _) => true
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

case class PlaybackWithRelatedData(
                                  playback: Playback,
                                  user: Option[User],
                                  )
