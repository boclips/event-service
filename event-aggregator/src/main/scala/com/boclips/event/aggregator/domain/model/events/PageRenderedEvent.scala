package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.Url
import com.boclips.event.aggregator.domain.model.users.UserIdentity
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.PagesRenderedFormatter
import com.boclips.event.infrastructure.EventFields

case class PageRenderedEvent
(
  timestamp: ZonedDateTime,
  userIdentity: UserIdentity,
  url: Option[Url],
  viewportWidth: Option[Int],
  viewportHeight: Option[Int],
  isResize: Boolean,
) extends Event {
  override val typeName: String = EventFields.Type.PAGE_RENDERED
  override val subtype: Option[String] = None
}

object PageRenderedEvent {
  implicit val formatter: RowFormatter[PageRenderedEvent] = PagesRenderedFormatter
}


