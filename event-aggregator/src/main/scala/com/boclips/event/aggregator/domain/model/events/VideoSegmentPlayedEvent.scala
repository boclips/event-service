package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{DeviceId, Query, Url, UserId, VideoId}

case class VideoSegmentPlayedEvent
(
  id: String,
  timestamp: ZonedDateTime,
  userId: UserId,
  query: Option[Query],
  url: Option[Url],
  videoId: VideoId,
  videoIndex: Option[Int],
  deviceId: Option[DeviceId],
  refererId: Option[UserId],
  secondsWatched: Int
) extends Event {
  override val typeName: String = EventConstants.VIDEO_SEGMENT_PLAYED
  override val subtype: Option[String] = None
}
