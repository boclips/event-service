package com.boclips.event.service.application

import com.boclips.event.service.domain.model.Event
import com.boclips.event.service.infrastructure.MongoEventConsumer
import com.boclips.event.service.infrastructure.mixpanel.MixpanelEventConsumer
import com.boclips.event.service.presentation.EventResource
import com.boclips.security.utils.UserExtractor

class TrackEvent(private val mongoEventConsumer: MongoEventConsumer, private val mixpanelEventConsumer: MixpanelEventConsumer) {
    operator fun invoke(eventResource: EventResource) {
        val event = Event(
                type = eventResource.type,
                properties = eventResource.properties,
                userID = UserExtractor.getCurrentUser()?.id
        )

        mongoEventConsumer.consumeEvent(event)
        mixpanelEventConsumer.consumeEvent(event)
    }
}