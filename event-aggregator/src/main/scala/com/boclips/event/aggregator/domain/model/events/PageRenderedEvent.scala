package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{DeviceId, Url, UserId}
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.PagesRenderedFormatter
import com.boclips.event.infrastructure.EventFields

case class PageRenderedEvent
(
  timestamp: ZonedDateTime,
  userId: Option[UserId],
  url: Option[Url],
) extends Event {
  override val deviceId: Option[DeviceId] = None
  override val typeName: String = EventFields.Type.PAGE_RENDERED
  override val subtype: Option[String] = None
}

object PageRenderedEvent {
  implicit val formatter: RowFormatter[PageRenderedEvent] = PagesRenderedFormatter
}


