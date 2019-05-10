package com.boclips.event.service.application

import com.boclips.event.service.domain.EventWriter
import com.boclips.events.config.Subscriptions.USER_ACTIVATED
import com.boclips.events.types.UserActivated
import org.springframework.cloud.stream.annotation.StreamListener

class PersistEvent(private val eventWriter: EventWriter) {

    @StreamListener(USER_ACTIVATED)
    fun userActivated(userActivated: UserActivated) {
        eventWriter.writeUserActivated(userActivated)
    }
}
