package com.boclips.event.aggregator.domain.model

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.events.Event

case class Session(
                    user: UserOrAnonymous,
                    events: List[Event]
                  ) {

  def start: ZonedDateTime = events.minBy(_.timestamp.toEpochSecond).timestamp

  def end: ZonedDateTime = events.maxBy(_.timestamp.toEpochSecond).timestamp
}
