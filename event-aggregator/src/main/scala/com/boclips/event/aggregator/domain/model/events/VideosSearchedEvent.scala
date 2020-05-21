package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._

case class VideosSearchedEvent
(
  timestamp: ZonedDateTime,
  userId: UserId,
  query: Query,
  url: Option[Url],
  videoResults: Option[Iterable[VideoId]],
  pageIndex: Int,
  totalResults: Int
) extends Event {
  override val deviceId: Option[DeviceId] = None
  override val typeName: String = EventConstants.VIDEOS_SEARCHED
  override val subtype: Option[String] = None
}


