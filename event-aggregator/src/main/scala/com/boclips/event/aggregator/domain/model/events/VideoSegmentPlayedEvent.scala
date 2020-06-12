package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.infrastructure.EventFields

case class VideoSegmentPlayedEvent
(
  id: String,
  timestamp: ZonedDateTime,
  userIdentity: UserIdentity,
  query: Option[Query],
  url: Option[Url],
  videoId: VideoId,
  videoIndex: Option[Int],
  refererId: Option[UserId],
  secondsWatched: Int
) extends Event {
  override val typeName: String = EventFields.Type.VIDEO_SEGMENT_PLAYED
  override val subtype: Option[String] = None
}
