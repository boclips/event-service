package com.boclips.event.aggregator.domain.model.users

import java.time.{YearMonth, ZonedDateTime}

case class UserId(value: String) extends Ordered[UserId] {
  override def compare(that: UserId): Int = value.compare(that.value)
}

case class DeviceId(value: String)

case class User(
                 identity: UserIdentity,
                 firstName: Option[String],
                 lastName: Option[String],
                 email: Option[String],
                 role: Option[String],
                 subjects: List[String],
                 ages: List[Int],
                 createdAt: ZonedDateTime,
                 organisation: Option[Organisation],
                 profileSchool: Option[Organisation],
                 isBoclipsEmployee: Boolean,
                 hasOptedIntoMarketing: Option[Boolean],
                 marketingUtmSource: Option[String],
                 marketingUtmTerm: Option[String],
                 marketingUtmCampaign: Option[String],
                 marketingUtmMedium: Option[String],
                 marketingUtmContent: Option[String],
                 externalUserId: Option[String],
               )

case class UserActiveStatus(month: YearMonth, isActive: Boolean)




