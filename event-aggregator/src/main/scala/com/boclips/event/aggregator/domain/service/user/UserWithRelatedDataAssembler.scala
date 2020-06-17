package com.boclips.event.aggregator.domain.service.user

import java.time.{LocalDate, YearMonth}

import com.boclips.event.aggregator._
import com.boclips.event.aggregator.domain.model._
import org.apache.spark.rdd.RDD

object UserWithRelatedDataAssembler {

  def apply(users: RDD[User], playbacks: RDD[Playback], searches: RDD[Search], sessions: RDD[Session]): RDD[UserWithRelatedData] = {

    val playbacksByUser: RDD[(UserId, Iterable[Playback])] = playbacks
      .filter(p => p.user.boclipsId.isDefined)
      .keyBy(_.user.boclipsId.get)
      .groupByKey()

    val referredPlaybacksByUser: RDD[(UserId, Iterable[Playback])] = playbacks
      .filter(p => p.refererId.isDefined && p.isShare)
      .keyBy(p => p.refererId.get)
      .groupByKey()

    val searchesByUser: RDD[(UserId, Iterable[Search])] = searches
      .keyBy(_.request.userId)
      .groupByKey()

    val activeMonthsByUser: RDD[(UserId, Iterable[YearMonth])] = playbacks
      .filter(p => p.user.boclipsId.isDefined)
      .map(playback => (playback.user.boclipsId.get, YearMonth.from(playback.timestamp)))
      .distinct()
      .groupByKey()

    val sessionsByUser: RDD[(UserId, Iterable[Session])] = sessions
      .filter(p => p.user.boclipsId.isDefined)
      .keyBy(_.user.boclipsId.get)
      .groupByKey()

    users
      .keyBy(_.id)
      .leftOuterJoin(playbacksByUser)
      .leftOuterJoin(referredPlaybacksByUser)
      .leftOuterJoin(searchesByUser)
      .leftOuterJoin(activeMonthsByUser)
      .leftOuterJoin(sessionsByUser)
      .values
      .map {
        case (((((user, playbacks), referredPlaybacks), searches), monthsActive), sessions) => {
          UserWithRelatedData.from(
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
