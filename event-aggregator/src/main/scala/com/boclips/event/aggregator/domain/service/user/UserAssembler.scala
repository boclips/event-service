package com.boclips.event.aggregator.domain.service.user

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model.{BoclipsUserIdentity, ExternalUserIdentity, User, UserIdentity}
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

    users union usersWithExternalIdentities
  }

}
