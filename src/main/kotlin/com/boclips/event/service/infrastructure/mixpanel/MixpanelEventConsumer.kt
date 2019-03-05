package com.boclips.event.service.infrastructure.mixpanel

import com.boclips.event.service.domain.model.Event
import com.boclips.event.service.infrastructure.EventToBsonConverter
import com.mixpanel.mixpanelapi.ClientDelivery
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import org.json.JSONObject
import java.util.*

open class MixpanelEventConsumer(private val mixpanel: MixpanelAPI = MixpanelAPI(), projectToken: String) {
    private val messageBuilder = MessageBuilder(projectToken)

    open fun consumeEvent(event: Event) {
        mixpanel.deliver(ClientDelivery().apply {
            EventToMixpanelMessageConverter().invoke(messageBuilder, event)
        })
    }
}
