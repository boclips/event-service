package com.boclips.event.aggregator.domain.service.session

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model._
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

class SessionAssembler(events: RDD[_ <: Event], users: RDD[User], dataDescription: String) {

  def assembleSessions(): RDD[Session] = {

    def sessionOwner(userOrDeviceId: UserOrDeviceId, optionUser: Option[User]): UserOrAnonymous = {
      userOrDeviceId match {
        case deviceId: DeviceId => AnonymousUser(deviceId = deviceId)
        case userId: UserId => optionUser.getOrElse(AnonymousUser(deviceId = DeviceId(userId.value)))
      }
    }

    val userById: RDD[(UserOrDeviceId, User)] = users.keyBy(_.id)

    events
      .groupBy(Event.uniqueUserOrDeviceId)
      .leftOuterJoin(userById)
      .flatMap { case (userOrDeviceId, (userEvents, optionUser)) => new SessionService().separateEventsIntoSessions(sessionOwner(userOrDeviceId, optionUser), userEvents) }
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName(s"Sessions ($dataDescription)")
  }


}
