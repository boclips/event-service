package com.boclips.event.aggregator.domain.model.sessions

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model.users.UserIdentity

case class Session(
                    user: UserIdentity,
                    events: List[Event]
                  ) {

  def start: ZonedDateTime = events.minBy(_.timestamp.toEpochSecond).timestamp

  def end: ZonedDateTime = events.maxBy(_.timestamp.toEpochSecond).timestamp
}
