package com.boclips.event.aggregator.domain.model

import java.time.{LocalDate, YearMonth, ZonedDateTime}

import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.UserFormatter

sealed trait UserIdentity {
  def boclipsId: Option[UserId]
  def deviceId: Option[DeviceId]
}

case class BoclipsUserIdentity(
                                id: UserId
                              ) extends UserIdentity {
  override def boclipsId: Option[UserId] = Some(id)

  override def deviceId: Option[DeviceId] = None

}

case class AnonymousUserIdentity(
                                deviceId: Option[DeviceId]
                                ) extends UserIdentity {
  override def boclipsId: Option[UserId] = None
}

case class UserId(value: String) extends Ordered[UserId] {
  override def compare(that: UserId): Int = value.compare(that.value)
}

case class DeviceId(value: String)

case class User(
                 id: UserId,
                 firstName: Option[String],
                 lastName: Option[String],
                 email: Option[String],
                 role: Option[String],
                 subjects: List[String],
                 ages: List[Int],
                 createdAt: ZonedDateTime,
                 organisation: Option[Organisation],
                 isBoclipsEmployee: Boolean,
                 hasOptedIntoMarketing: Option[Boolean]
               )

case class UserActiveStatus(month: YearMonth, isActive: Boolean)

case class UserWithRelatedData(
                                user: User,
                                monthlyActiveStatus: List[UserActiveStatus],
                                playbacks: List[Playback],
                                referredPlaybacks: List[Playback],
                                searches: List[Search],
                                sessions: List[Session],
                              )

object UserWithRelatedData {

  implicit val formatter: RowFormatter[UserWithRelatedData] = UserFormatter

  def from(
            user: User,
            playbacks: List[Playback],
            referredPlaybacks: List[Playback],
            searches: List[Search],
            sessions: List[Session],
            monthsActive: List[YearMonth],
            until: LocalDate
          ): UserWithRelatedData = {
    UserWithRelatedData(
      user = user,
      monthlyActiveStatus = this.monthlyActiveStatus(signupMonth = YearMonth.from(user.createdAt), currentMonth = YearMonth.from(until), monthsActive = monthsActive.toSet),
      playbacks = playbacks,
      referredPlaybacks = referredPlaybacks,
      searches = searches,
      sessions = sessions,
    )
  }

  private def monthlyActiveStatus(signupMonth: YearMonth, currentMonth: YearMonth, monthsActive: Set[YearMonth]): List[UserActiveStatus] = {
    monthsBetween(signupMonth, currentMonth).map(month =>
      UserActiveStatus(month = month, isActive = monthsActive.contains(month) || monthsActive.contains(month.minusMonths(1)) || monthsActive.contains(month.minusMonths(2)))
    )
  }

  private def monthsBetween(start: YearMonth, end: YearMonth): List[YearMonth] = {
    if (start == end) List(start) else start :: monthsBetween(start.plusMonths(1), end)
  }
}
