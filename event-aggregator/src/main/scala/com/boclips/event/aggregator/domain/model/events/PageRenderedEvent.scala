package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{DeviceId, Url, UserId}
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.PagesRenderedFormatter

case class PageRenderedEvent
(
  timestamp: ZonedDateTime,
  userId: UserId,
  url: Option[Url],
) extends Event {
  override val deviceId: Option[DeviceId] = None
  override val typeName: String = EventConstants.PAGE_RENDERED
  override val subtype: Option[String] = None
}

object PageRenderedEvent {
  implicit val formatter: RowFormatter[PageRenderedEvent] = PagesRenderedFormatter
}

