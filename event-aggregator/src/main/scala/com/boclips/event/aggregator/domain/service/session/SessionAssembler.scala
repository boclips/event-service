package com.boclips.event.aggregator.domain.service.session

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model.sessions.Session
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

class SessionAssembler(events: RDD[_ <: Event], dataDescription: String) {

  def assembleSessions(): RDD[Session] = {

    events
      .groupBy(_.userIdentity)
      .flatMap {
        case (userIdentity, userEvents) => new SessionService()
          .separateEventsIntoSessions(userIdentity, userEvents)
      }
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName(s"Sessions ($dataDescription)")
  }


}
