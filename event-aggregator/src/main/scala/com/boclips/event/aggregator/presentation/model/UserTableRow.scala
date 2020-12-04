package com.boclips.event.aggregator.presentation.model

import java.time.{LocalDate, YearMonth}

import com.boclips.event.aggregator.domain.model.events.VideoInteractedWithEvent
import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.search.Search
import com.boclips.event.aggregator.domain.model.sessions.Session
import com.boclips.event.aggregator.domain.model.users.{User, UserActiveStatus}
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.UserFormatter

object UserTableRow {

  implicit val formatter: RowFormatter[UserTableRow] = UserFormatter

  def from(
            user: User,
            playbacks: List[Playback],
            referredPlaybacks: List[Playback],
            searches: List[Search],
            sessions: List[Session],
            monthsActive: List[YearMonth],
            interactions: List[VideoInteractedWithEvent],
            until: LocalDate
          ): UserTableRow = {
    UserTableRow(
      user = user,
      monthlyActiveStatus = this.monthlyActiveStatus(signupMonth = YearMonth.from(user.createdAt), currentMonth = YearMonth.from(until), monthsActive = monthsActive.toSet),
      playbacks = playbacks,
      referredPlaybacks = referredPlaybacks,
      searches = searches,
      sessions = sessions,
      interactions = interactions,
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

case class UserTableRow(
                         user: User,
                         monthlyActiveStatus: List[UserActiveStatus],
                         playbacks: List[Playback],
                         referredPlaybacks: List[Playback],
                         searches: List[Search],
                         sessions: List[Session],
                         interactions: List[VideoInteractedWithEvent]
                       )
