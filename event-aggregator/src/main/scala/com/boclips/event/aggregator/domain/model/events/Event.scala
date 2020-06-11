package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{DeviceId, Url, UserId, UserOrDeviceId}

trait Event {
  val timestamp: ZonedDateTime
  val userId: Option[UserId]
  val deviceId: Option[DeviceId]
  val url: Option[Url]
  val typeName: String
  val subtype: Option[String]
}

object Event {
  def uniqueUserOrDeviceId(event: Event): UserOrDeviceId = {
    event.userId match {
      case Some(userId) => userId
      case None => event.deviceId.getOrElse(DeviceId("UNKNOWN"))
    }
  }
}
