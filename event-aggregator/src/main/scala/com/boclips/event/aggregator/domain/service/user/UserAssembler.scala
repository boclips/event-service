package com.boclips.event.aggregator.domain.service.user

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model.users._
import org.apache.spark.rdd.RDD

object UserAssembler {

  def apply(users: RDD[User], events: RDD[Event]): RDD[User] = {
    val externalIdentityByBoclipsIdentity = events
      .map(_.userIdentity)
      .flatMap {
        case identity: ExternalUserIdentity => Some(identity)
        case _ => None
      }
      .distinct()
      .keyBy[UserIdentity](identity => BoclipsUserIdentity(identity.boclipsId))

    val usersWithExternalIdentities = users.keyBy(user => user.identity)
      .join(externalIdentityByBoclipsIdentity)
      .values
      .map {
        case (user, externalUserIdentity) => user.copy(identity = externalUserIdentity)
      }

    val usersWithAnonymousIdentities = events
      .map(event => (event.userIdentity, event.timestamp))
      .flatMap {
        case (AnonymousUserIdentity(Some(deviceId)), timestamp) => Some((AnonymousUserIdentity(Some(deviceId)), timestamp))
        case _ => None
      }
      .reduceByKey((t1, t2) => if (t1.compareTo(t2) > 0) t2 else t1)
      .map {
        case (identity, timestamp) => anonymousUser(identity, timestamp)
      }

    users union usersWithExternalIdentities union usersWithAnonymousIdentities
  }

  def anonymousUser(identity: AnonymousUserIdentity, firstEventTimestamp: ZonedDateTime): User = {
    User(
      identity = identity,
      createdAt = firstEventTimestamp,
      firstName = None,
      lastName = None,
      email = None,
      role = None,
      subjects = Nil,
      ages = Nil,
      organisation = None,
      isBoclipsEmployee = false,
      hasOptedIntoMarketing = None,
      profileSchool = None,
      marketingUtmCampaign = None,
      marketingUtmContent = None,
      marketingUtmMedium = None,
      marketingUtmSource = None,
      marketingUtmTerm = None,
      externalUserId = None,
    )
  }

}
