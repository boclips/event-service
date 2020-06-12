package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.Url

trait GenericEvent[TUserIdentity] {
  val timestamp: ZonedDateTime
  val userIdentity: TUserIdentity
  val url: Option[Url]
  val typeName: String

  val subtype: Option[String]
}

