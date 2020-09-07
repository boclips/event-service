package com.boclips.event.aggregator.domain.service.playback

import java.time.Duration

import com.boclips.event.aggregator.domain.model.events.VideoSegmentPlayedEvent
import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.sessions.Session
import com.boclips.event.aggregator.domain.model.users.UserIdentity

class SessionPlaybackAssembler {

  def assemblePlaybacksInSession(session: Session, durationSecondsByVideoId: Map[String, Int]): Iterable[Playback] = {
    session
      .events
      .flatMap {
        case segmentPlayedEvent: VideoSegmentPlayedEvent => Some(segmentPlayedEvent)
        case _ => None
      }
      .groupBy(_.videoId)
      .map {
        case (videoId, events) => combineSameVideoEvents(events, Duration.ofSeconds(durationSecondsByVideoId.getOrElse(videoId.value, 0).toLong), session.user)
      }
  }

  private def combineSameVideoEvents(events: Iterable[VideoSegmentPlayedEvent], videoDuration: Duration, user: UserIdentity): Playback = {
    val firstEvent = events.head
    val timestamp = events.map(_.timestamp).minBy(_.toEpochSecond)
    val secondsWatched = events.map(_.secondsWatched).sum
    val refererId = events.flatMap(_.refererId).headOption
    Playback(
      id = firstEvent.id,
      timestamp = timestamp,
      user = user,
      videoId = firstEvent.videoId,
      secondsWatched = secondsWatched,
      url = firstEvent.url,
      refererId = refererId,
      deviceId = firstEvent.userIdentity.deviceId,
      videoDuration = videoDuration,
    )
  }
}
