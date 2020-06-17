package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.Url
import com.boclips.event.aggregator.domain.model.users.UserIdentity

case class OtherEvent
(
  timestamp: ZonedDateTime,
  typeName: String,
  userIdentity: UserIdentity,
) extends Event {

  override val subtype: Option[String] = None
  override val url: Option[Url] = None
}
