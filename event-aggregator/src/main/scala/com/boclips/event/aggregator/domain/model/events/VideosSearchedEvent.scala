package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.infrastructure.EventFields

case class VideosSearchedEvent
(
  timestamp: ZonedDateTime,
  userIdPresent: UserId,
  query: Query,
  url: Option[Url],
  videoResults: Option[Iterable[VideoId]],
  pageIndex: Int,
  totalResults: Int
) extends Event {
  override val deviceId: Option[DeviceId] = None
  override val typeName: String = EventFields.Type.VIDEOS_SEARCHED
  override val subtype: Option[String] = None
  override val userId: Option[UserId] = Some(userIdPresent)
}


