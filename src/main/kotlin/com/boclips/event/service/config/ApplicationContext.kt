package com.boclips.event.service.config

import com.boclips.event.service.application.PersistEvent
import com.boclips.event.service.application.UpdateVideo
import com.boclips.event.service.domain.EventRepository
import com.boclips.event.service.domain.VideoRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationContext(
    private val eventRepository: EventRepository,
    private val videoRepository: VideoRepository
) {

    @Bean
    fun persistEvent(): PersistEvent {
        return PersistEvent(eventRepository)
    }

    @Bean
    fun updateVideo(): UpdateVideo {
        return UpdateVideo(videoRepository)
    }

}
