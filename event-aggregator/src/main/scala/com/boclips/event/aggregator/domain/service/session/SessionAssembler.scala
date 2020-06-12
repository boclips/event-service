package com.boclips.event.aggregator.domain.service.session

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events.Event
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

class SessionAssembler(events: RDD[_ <: Event], users: RDD[User], dataDescription: String) {

  def assembleSessions(): RDD[Session] = {

    def sessionOwner(identifier: UniqueUserIdentifier, optionUser: Option[User]): UserOrAnonymous = {
      identifier match {
        case deviceId: DeviceId => AnonymousUser(deviceId = Some(deviceId))
        case userId: UserId => optionUser.getOrElse(AnonymousUser(deviceId = Some(DeviceId(userId.value))))
      }
    }

    val userById: RDD[(UniqueUserIdentifier, User)] = users.keyBy(_.id)

    events
      .groupBy(_.userIdentity.mostSpecificIdentifier)
      .leftOuterJoin(userById)
      .flatMap {
        case (userOrDeviceId, (userEvents, optionUser)) => new SessionService()
          .separateEventsIntoSessions(sessionOwner(userOrDeviceId, optionUser), userEvents) }
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName(s"Sessions ($dataDescription)")
  }


}
