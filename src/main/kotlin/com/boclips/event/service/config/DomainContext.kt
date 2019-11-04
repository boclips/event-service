package com.boclips.event.service.config

import com.boclips.event.service.domain.EventRepository
import com.boclips.event.service.domain.EventWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainContext(private val eventWriter: EventWriter) {

    @Bean
    fun eventRepository(): EventRepository {
        return EventRepository(eventWriter)
    }
}
