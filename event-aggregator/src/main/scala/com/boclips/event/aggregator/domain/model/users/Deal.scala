package com.boclips.event.aggregator.domain.model.users

import java.time.ZonedDateTime

case class Deal(
                 billing: Boolean,
                 dealExpiresAt: Option[ZonedDateTime],
               )
