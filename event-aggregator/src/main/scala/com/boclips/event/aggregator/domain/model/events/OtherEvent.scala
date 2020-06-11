package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{DeviceId, Url, UserId}

case class OtherEvent
(
  timestamp: ZonedDateTime,
  typeName: String,
  userId: Option[UserId],
) extends Event {

  override val deviceId: Option[DeviceId] = None
  override val subtype: Option[String] = None
  override val url: Option[Url] = None
}
