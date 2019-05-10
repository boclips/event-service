package com.boclips.event.service.config

import com.boclips.event.service.application.PersistEvent
import com.boclips.event.service.domain.EventWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationContext(private val eventWriter: EventWriter) {

    @Bean
    fun persistEvent(): PersistEvent {
        return PersistEvent(eventWriter)
    }

}
