package com.boclips.event.service.config

import com.boclips.event.service.domain.*
import com.boclips.event.service.infrastructure.mongodb.*
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.litote.kmongo.KMongo
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InfrastructureContext(val mongoProperties: MongoProperties) {
    @Bean
    fun mongoClient(): MongoClient {
        return KMongo.createClient(MongoClientURI(mongoProperties.determineUri()))
    }

    @Bean
    fun eventWriter(): EventWriter {
        return MongoEventWriter(mongoClient())
    }

    @Bean
    fun videoRepository(): VideoRepository {
        return MongoVideoRepository(mongoClient())
    }

    @Bean
    fun userRepository(): UserRepository {
        return MongoUserRepository(mongoClient())
    }

    @Bean
    fun contractRepository(): ContractRepository =
        MongoContractRepository(mongoClient())

    @Bean
    fun contentPackageRepository(): ContentPackageRepository =
        MongoContentPackageRepository(mongoClient())

    @Bean
    fun collectionRepository(): CollectionRepository {
        return MongoCollectionRepository(mongoClient())
    }

    @Bean
    fun orderRepository(): OrderRepository {
        return MongoOrderRepository(mongoClient())
    }

    @Bean
    fun channelRepository(): ChannelRepository {
        return MongoChannelRepository(mongoClient())
    }
    @Bean
    fun contractLegalRestrictionsRepository(): ContractLegalRestrictionsRepository {
        return MongoContractLegalRestrictionsRepository(mongoClient())
    }
}
