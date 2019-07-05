package com.boclips.event.service.config

import com.boclips.event.service.domain.EventRepository
import com.boclips.event.service.infrastructure.MongoEventRepository
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
    fun eventWriter(): EventRepository {
        return MongoEventRepository(mongoClient())
    }

}
