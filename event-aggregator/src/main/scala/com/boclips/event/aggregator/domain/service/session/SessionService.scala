package com.boclips.event.aggregator.domain.service.session

import java.time.Duration

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model.{Session, UserIdentity}

class SessionService {

  def separateEventsIntoSessions(owner: UserIdentity, events: Iterable[Event]): Iterable[Session] = {
    events.toList
      .sortBy(_.timestamp.toEpochSecond)
      .foldLeft(List(List[Event]()))(buildSessions)
      .map(Session(owner, _))
  }

  private def buildSessions(sessions: List[List[Event]], event: Event): List[List[Event]] = {
    val currentSession :: previousSessions = sessions
    currentSession.headOption match {
      case None => List(List(event))
      case Some(previousEvent) => if (theSameSession(previousEvent, event)) {
        (event :: currentSession) :: previousSessions
      } else {
        List(event) :: sessions
      }
    }
  }

  private def theSameSession(e1: Event, e2: Event): Boolean = {
    Duration.between(e1.timestamp, e2.timestamp).minusHours(1).isNegative
  }

}
