package com.boclips.event.aggregator.testsupport.testfactories

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model.sessions
import com.boclips.event.aggregator.domain.model.sessions.Session
import com.boclips.event.aggregator.domain.model.users.UserIdentity

object SessionFactory {

  def createSession(
                     user: UserIdentity = UserFactory.createBoclipsUserIdentity(),
                     events: List[Event]
                   ): Session = sessions.Session(user, events)
}
