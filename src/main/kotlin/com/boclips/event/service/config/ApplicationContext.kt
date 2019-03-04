package com.boclips.event.service.config

import com.boclips.event.service.application.TrackEvent
import com.boclips.event.service.infrastructure.MongoEventConsumer
import com.boclips.event.service.infrastructure.mixpanel.MixpanelEventConsumer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationContext {

    @Bean
    fun trackEvent(
            mongoEventConsumer: MongoEventConsumer,
            mixpanelEventConsumer: MixpanelEventConsumer
    ) = TrackEvent(mongoEventConsumer, mixpanelEventConsumer)
}