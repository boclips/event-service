package com.boclips.event.service.config

import com.boclips.event.service.application.PersistEvent
import com.boclips.event.service.application.UpdateChannel
import com.boclips.event.service.application.UpdateCollection
import com.boclips.event.service.application.UpdateContract
import com.boclips.event.service.application.UpdateOrder
import com.boclips.event.service.application.UpdateUser
import com.boclips.event.service.application.UpdateVideo
import com.boclips.event.service.domain.ChannelRepository
import com.boclips.event.service.domain.CollectionRepository
import com.boclips.event.service.domain.ContractRepository
import com.boclips.event.service.domain.EventRepository
import com.boclips.event.service.domain.OrderRepository
import com.boclips.event.service.domain.UserRepository
import com.boclips.event.service.domain.VideoRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationContext(
    private val eventRepository: EventRepository,
    private val videoRepository: VideoRepository,
    private val collectionRepository: CollectionRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val channelRepository: ChannelRepository,
    private val contractRepository: ContractRepository
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
    fun updateChannel(): UpdateChannel {
        return UpdateChannel(channelRepository)
    }

    @Bean
    fun updateContract(): UpdateContract =
        UpdateContract(contractRepository)

    @Bean
    fun updateOrder(): UpdateOrder {
        return UpdateOrder(orderRepository)
    }
}
