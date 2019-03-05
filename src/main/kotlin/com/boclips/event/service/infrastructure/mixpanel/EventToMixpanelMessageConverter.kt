package com.boclips.event.service.infrastructure.mixpanel

import com.boclips.event.service.domain.model.Event
import com.boclips.event.service.infrastructure.EventToBsonConverter
import com.mixpanel.mixpanelapi.MessageBuilder
import org.json.JSONObject
import java.util.*

class EventToMixpanelMessageConverter {
    operator fun invoke(messageBuilder: MessageBuilder, event: Event): JSONObject {
        return messageBuilder.event(
                event.userID ?: UUID.randomUUID().toString(),
                event.type,
                EventToBsonConverter.convert(event)
        )
    }
}