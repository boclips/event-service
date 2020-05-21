package com.boclips.event.aggregator.testsupport.testfactories

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model.{Session, UserOrAnonymous}
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createUser

object SessionFactory {

  def createSession(
                     user: UserOrAnonymous = createUser(),
                     events: List[Event]
                   ): Session = Session(user, events)
}
