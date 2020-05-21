package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.events.{Event, EventConstants}
import com.boclips.event.aggregator.domain.model.{SCHOOL_ORGANISATION, User, Video}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

case class Data(
                 events: RDD[_ <: Event],
                 users: RDD[User],
                 videos: RDD[Video],
                 dataDescription: String = ""
               ) {

  def schoolOnly(): Data = {
    val schoolUsers = this.schoolUsers()
    val schoolEvents = this.schoolEvents(schoolUsers)

    Data(schoolEvents, schoolUsers, videos, "School only")
  }

  private def schoolEvents(schoolUsers: RDD[User]): RDD[_ <: Event] = {
    events.keyBy(_.userId).leftOuterJoin(schoolUsers.keyBy(_.id))
      .flatMap {
        case (EventConstants.anonymousUserId, (event, _)) => Some(event)
        case (_, (event, Some(_))) => Some(event)
        case (_, (_, None)) => None
      }
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Events (school only)")
  }

  private def schoolUsers(): RDD[User] = {
    users
      .filter(user => user.organisation match {
        case Some(organisation) => organisation.`type` == SCHOOL_ORGANISATION
        case None =>  true
      })
      .setName("Users (school only)")
      .persist(StorageLevel.MEMORY_AND_DISK)
  }
}
