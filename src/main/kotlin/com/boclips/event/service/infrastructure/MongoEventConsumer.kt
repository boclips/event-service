package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.model.Event
import com.mongodb.MongoClient
import org.bson.Document

open class MongoEventConsumer(private val mongoClient: MongoClient) {
    companion object {
        const val DB_NAME = "event-service-db"
        const val COLLECTION_NAME = "events"
    }

    open fun consumeEvent(event: Event) {
        val json = EventToBsonConverter.convert(event)
        json.put("type", event.type)
        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME)
                .insertOne(Document.parse(json.toString()))
    }

}