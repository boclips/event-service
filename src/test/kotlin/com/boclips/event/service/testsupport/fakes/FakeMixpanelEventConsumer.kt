package com.boclips.event.service.testsupport.fakes

import com.boclips.event.service.domain.model.Event
import com.boclips.event.service.infrastructure.mixpanel.MixpanelEventConsumer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

class FakeMixpanelEventConsumer : MixpanelEventConsumer(projectToken = "") {
    val events = mutableListOf<Event>()

    override fun consumeEvent(event: Event) {
        events.add(event)
    }

    fun reset() {
        events.clear()
    }
}

@Profile("fake-mixpanel")
@Configuration
class FakeMixpanelEventConsumerConfiguration {

    @Bean
    @Primary
    fun fakeMixpanelEventConsumer(): FakeMixpanelEventConsumer {
        return FakeMixpanelEventConsumer()
    }
}