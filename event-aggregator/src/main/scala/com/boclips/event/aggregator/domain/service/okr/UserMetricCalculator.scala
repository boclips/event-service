package com.boclips.event.aggregator.domain.service.okr

import java.time.{LocalDate, ZoneOffset}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.service.okr.ActivityType.ActivityType
import com.boclips.event.aggregator.utils.Collections._
import org.apache.spark.rdd.RDD


object UserMetricCalculator {
  val daysToChurn: Int = 60

  def calculateMetrics(timePeriodDuration: TimePeriodDuration)(implicit events: RDD[_ <: Event], users: RDD[User]): List[UserMetric] = {
    events.sparkContext.setJobDescription("Calculating user metrics")

    val activeUsersByTimePeriod: Map[DateRange, ActiveUserCounts] = activeUserCounts(timePeriodDuration)

    val totalsByDate: Map[LocalDate, UserTotals] = userTotals(timePeriodDuration)

    val recentlyActiveByTime: Map[LocalDate, Long] = activeUsersByTimePeriod.keys.flatMap(timePeriod => List(timePeriod.startInclusive, timePeriod.endExclusive))
      .map(time => (time, countActiveUsers(time.minusDays(daysToChurn), time)))
      .toMap

    activeUsersByTimePeriod
      .map {
        case (timePeriod: DateRange, counts) => {
          UserMetric(
            timePeriod = timePeriod,
            activeUserCounts = counts,
            totalsAtStart = totalsByDate.getOrElse(timePeriod.startInclusive, UserTotals(timePeriod.startInclusive, 0)),
            totalsAtEnd = totalsByDate.getOrElse(timePeriod.endExclusive, UserTotals(timePeriod.endExclusive, 0)),
            recentlyActiveAtStart = recentlyActiveByTime.getOrElse(timePeriod.startInclusive, 0),
            recentlyActiveAtEnd = recentlyActiveByTime.getOrElse(timePeriod.endExclusive, 0)
          )
        }
      }
      .toList
  }

  def countActiveUsers(fromInclusive: LocalDate, toExclusive: LocalDate)(implicit events: RDD[_ <: Event]): Long = {
    events
      .filter(event => event.timestamp.isBefore(toExclusive.atStartOfDay(ZoneOffset.UTC)) && !event.timestamp.isBefore(fromInclusive.atStartOfDay(ZoneOffset.UTC)))
      .groupBy(event => event.userId)
      .values
      .map(userEvents => if (userEvents.nonEmpty) 1 else 0)
      .sum()
      .toInt
  }

  def userTotals(timePeriodDuration: TimePeriodDuration)(implicit users: RDD[_ <: User]): Map[LocalDate, UserTotals] = {
    users
      .map(user => timePeriodDuration.dateRangeOf(user.createdAt).endExclusive)
      .countByValue()
      .toList
      .sortBy(_._1.toEpochDay)
      .foldLeft[List[(LocalDate, Long)]](Nil) {
        case (Nil, (date, registrations)) => (date, registrations) :: Nil
        case ((prevDate, prevRegistrations) :: tail, (date, registrations)) => (date, prevRegistrations + registrations) :: (prevDate, prevRegistrations) :: tail
      }
      .map {
        case (date, cummulativeRegistrations) => (date, UserTotals(date, cummulativeRegistrations))
      }
      .toMap
  }

  def activeUserCounts(timePeriodDuration: TimePeriodDuration)(implicit events: RDD[_ <: Event]): Map[DateRange, ActiveUserCounts] = {
    def getUserTotals(activityType: ActivityType, count: Int): ActiveUserCounts = activityType match {
      case ActivityType.NEW => ActiveUserCounts(newUsers = count, repeatUsers = 0)
      case ActivityType.REPEAT => ActiveUserCounts(newUsers = 0, repeatUsers = count)
    }

    userActivityByTimePeriod(timePeriodDuration)
      .map { case UserActivityInTimePeriod(_, timePeriod, activityType) => (timePeriod, activityType) }
      .countByValue()
      .toList
      .map { case ((timePeriod: DateRange, activityType: ActivityType), count: Long) => (timePeriod, getUserTotals(activityType, count.toInt)) }
      .groupByKey()
      .mapValues(_.reduce(_ + _))
  }

  private def userActivityByTimePeriod(timePeriodDuration: TimePeriodDuration)(implicit events: RDD[_ <: Event]): RDD[UserActivityInTimePeriod] = {
    events
      .map(event => ActiveUserInTimePeriod(event.userId, timePeriodDuration.dateRangeOf(event.timestamp)))
      .distinct()
      .groupBy(activeUser => activeUser.userId)
      .values
      .map(userActivities => userActivities.toList.sortBy(_.timePeriod.startInclusive.toEpochDay))
      .flatMap {
        case firstTimePeriod :: otherTimePeriods =>
          firstTimePeriod.ofType(ActivityType.NEW) :: otherTimePeriods.map(_.ofType(ActivityType.REPEAT))
        case Nil => throw new Exception("this should never happen")
      }
  }
}

case class ActiveUsersSummary(totalUsers: Long, newUsers: Long, repeatUsers: Long)

object ActivityType extends Enumeration {
  type ActivityType = Value
  val NEW, REPEAT = Value
}

case class ActivityInTimePeriod(timePeriod: DateRange, activityType: ActivityType)

case class ActiveUserInTimePeriod(userId: UserId, timePeriod: DateRange) {
  def ofType(activityType: ActivityType) = UserActivityInTimePeriod(userId = userId, timePeriod = timePeriod, activityType = activityType)
}


case class UserActivityInTimePeriod(userId: UserId, timePeriod: DateRange, activityType: ActivityType)
