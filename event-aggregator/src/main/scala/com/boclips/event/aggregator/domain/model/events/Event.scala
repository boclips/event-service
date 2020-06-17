package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{Url, UserIdentity}

trait Event {
  val timestamp: ZonedDateTime
  val userIdentity: UserIdentity
  val url: Option[Url]
  val typeName: String

  val subtype: Option[String]
}

