package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{DeviceId, Url, UserId}

case class PlatformInteractedWithEvent(
                                        userId: UserId,
                                        timestamp: ZonedDateTime,
                                        url: Option[Url],
                                        subtype: Option[String],
                                      ) extends Event {
  override val typeName: String = EventConstants.PLATFORM_INTERACTED_WITH
  override val deviceId: Option[DeviceId] = None
}
