package com.boclips.event.aggregator.presentation.assemblers

import java.time._

import com.boclips.event.aggregator._
import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.search.Search
import com.boclips.event.aggregator.domain.model.sessions.Session
import com.boclips.event.aggregator.domain.model.users.{BoclipsUserIdentity, User, UserIdentity}
import com.boclips.event.aggregator.presentation.model.UserTableRow
import org.apache.spark.rdd.RDD

object UserTableRowAssembler {

  def apply(users: RDD[User], playbacks: RDD[Playback], searches: RDD[Search], sessions: RDD[Session]): RDD[UserTableRow] = {

    val playbacksByUser: RDD[(UserIdentity, Iterable[Playback])] = playbacks
      .keyBy(_.user)
      .groupByKey()

    val referredPlaybacksByUser: RDD[(UserIdentity, Iterable[Playback])] = playbacks
      .filter(p => p.refererId.isDefined && p.isShare)
      .keyBy[UserIdentity](p => BoclipsUserIdentity(p.refererId.get))
      .groupByKey()

    val searchesByUser: RDD[(UserIdentity, Iterable[Search])] = searches
      .keyBy[UserIdentity](it => it.request.userIdentity)
      .groupByKey()

    val activeMonthsByUser: RDD[(UserIdentity, Iterable[YearMonth])] = playbacks
      .filter(p => p.user.id.isDefined)
      .map(playback => (playback.user, YearMonth.from(playback.timestamp)))
      .distinct()
      .groupByKey()

    val sessionsByUser: RDD[(UserIdentity, Iterable[Session])] = sessions
      .filter(p => p.user.id.isDefined)
      .keyBy(_.user)
      .groupByKey()

    users
      .keyBy(_.identity)
      .leftOuterJoin(playbacksByUser)
      .leftOuterJoin(referredPlaybacksByUser)
      .leftOuterJoin(searchesByUser)
      .leftOuterJoin(activeMonthsByUser)
      .leftOuterJoin(sessionsByUser)
      .values
      .map {
        case (((((user, playbacks), referredPlaybacks), searches), monthsActive), sessions) => {
          UserTableRow.from(
            user = user,
            playbacks = playbacks,
            referredPlaybacks = referredPlaybacks,
            searches = searches,
            sessions = sessions,
            monthsActive = monthsActive,
            until = LocalDate.now(),
          )
        }
      }
  }
}
