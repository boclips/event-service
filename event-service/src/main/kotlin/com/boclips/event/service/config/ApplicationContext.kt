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
    private val orderRepository: OrderRepository,
    private val channelRepository: ChannelRepository,
    private val contractRepository: ContractRepository,
    private val contractLegalRestrictionsRepository: ContractLegalRestrictionsRepository,
    private val contentPackageRepository: ContentPackageRepository
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
    fun updateContentPackage(): UpdateContentPackage {
        return UpdateContentPackage(contentPackageRepository)
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
    @Bean
    fun updateContractLegalRestriction(): UpdatedContractLegalRestriction {
        return UpdatedContractLegalRestriction(contractLegalRestrictionsRepository)
    }
}
