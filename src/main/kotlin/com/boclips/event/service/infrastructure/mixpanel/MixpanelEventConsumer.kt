package com.boclips.event.service.infrastructure.mixpanel

import com.boclips.event.service.domain.model.Event
import com.boclips.event.service.domain.service.EventConsumer
import com.boclips.event.service.infrastructure.EventToBsonConverter
import com.mixpanel.mixpanelapi.ClientDelivery
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import org.json.JSONObject
import java.util.*

class MixpanelEventConsumer(projectToken: String) : EventConsumer {
    private val mixpanel = MixpanelAPI()
    private val messageBuilder = MessageBuilder(projectToken)

    override fun consumeEvent(events: List<Event>) = events.map(this::eventToMessage)
            .fold(ClientDelivery()) { delivery, msg -> delivery.apply { addMessage(msg) } }
            .let(mixpanel::deliver)

    private fun eventToMessage(event: Event): JSONObject = messageBuilder.event(
            UUID.randomUUID().toString(),
            event.type,
            EventToBsonConverter.convert(event)
    )
}
