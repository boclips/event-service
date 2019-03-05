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
        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME)
                .insertOne(eventToDocument(event))
    }

    private fun eventToDocument(event: Event): Document {
        val json = EventToBsonConverter.convert(event)
        json.put("type", event.type)
        json.put("userId", event.userID)

        return Document.parse(json.toString())
    }
}