package com.boclips.event.aggregator.domain.model.users

case class Organisation(
                         name: String,
                         postcode: Option[String],
                         state: Option[String],
                         countryCode: Option[String],
                         `type`: OrganisationType,
                         tags: Set[String],
                         parent: Option[Organisation],
                         deal: Deal,
                         features: Option[Map[String, Boolean]]
                       )
