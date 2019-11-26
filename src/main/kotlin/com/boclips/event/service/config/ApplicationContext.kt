package com.boclips.event.service.config

import com.boclips.event.service.application.*
import com.boclips.event.service.domain.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationContext(
    private val eventRepository: EventRepository,
    private val videoRepository: VideoRepository,
    private val collectionRepository: CollectionRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) {

    @Bean
    fun persistEvent(): PersistEvent {
        return PersistEvent(eventRepository)
    }

    @Bean
    fun updateVideo(): UpdateVideo {
        return UpdateVideo(videoRepository)
    }

    @Bean
    fun updateUser(): UpdateUser {
        return UpdateUser(userRepository)
    }

    @Bean
    fun updateCollection(): UpdateCollection {
        return UpdateCollection(collectionRepository)
    }

    @Bean
    fun updateOrder(): UpdateOrder {
        return UpdateOrder(orderRepository)
    }

}
