package com.boclips.event.aggregator.testsupport.testfactories

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.model.{Session, UserIdentity, UserOrAnonymous}
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createUser

object SessionFactory {

  def createSession(
                     user: UserIdentity = UserFactory.createBoclipsUserIdentity(),
                     events: List[Event]
                   ): Session = Session(user, events)
}
