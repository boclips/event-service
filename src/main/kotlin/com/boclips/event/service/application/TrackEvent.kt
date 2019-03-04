package com.boclips.event.service.application

import com.boclips.event.service.domain.model.Event
import com.boclips.event.service.infrastructure.MongoEventConsumer
import com.boclips.event.service.infrastructure.mixpanel.MixpanelEventConsumer

class TrackEvent(private val mongoEventConsumer: MongoEventConsumer, private val mixpanelEventConsumer: MixpanelEventConsumer) {
    operator fun invoke(event: Event) {
        mongoEventConsumer.consumeEvent(event)
        mixpanelEventConsumer.consumeEvent(event)
    }
}