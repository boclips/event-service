package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.Url
import com.boclips.event.aggregator.domain.model.users.UserIdentity
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.PlatformInteractedWithEventFormatter
import com.boclips.event.infrastructure.EventFields

case class PlatformInteractedWithEvent(
                                        userIdentity: UserIdentity,
                                        timestamp: ZonedDateTime,
                                        url: Option[Url],
                                        subtype: Option[String],
                                      ) extends Event {
  override val typeName: String = EventFields.Type.PLATFORM_INTERACTED_WITH
}

object PlatformInteractedWithEvent {
  implicit val formatter: RowFormatter[PlatformInteractedWithEvent] = PlatformInteractedWithEventFormatter
}

