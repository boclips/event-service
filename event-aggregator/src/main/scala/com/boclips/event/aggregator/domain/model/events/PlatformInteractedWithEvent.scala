package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{DeviceId, Url, UserId}
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.PlatformInteractedWithEventFormatter
import com.boclips.event.infrastructure.EventFields

case class PlatformInteractedWithEvent(
                                        userId: Option[UserId],
                                        timestamp: ZonedDateTime,
                                        url: Option[Url],
                                        subtype: Option[String],
                                      ) extends Event {
  override val typeName: String = EventFields.Type.PLATFORM_INTERACTED_WITH
  override val deviceId: Option[DeviceId] = None
}

object PlatformInteractedWithEvent {
  implicit val formatter: RowFormatter[PlatformInteractedWithEvent] = PlatformInteractedWithEventFormatter
}

