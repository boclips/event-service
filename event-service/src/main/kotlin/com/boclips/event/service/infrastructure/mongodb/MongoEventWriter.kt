package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.domain.EventWriter
import com.mongodb.MongoClient
import mu.KLogging
import org.bson.Document

class MongoEventWriter(private val mongoClient: MongoClient) : EventWriter {
    companion object : KLogging() {
        const val COLLECTION_NAME = "events"
    }

    override fun write(event: Map<String, Any>) {
        try {
            getCollection().insertOne(Document(event))
        } catch (e: Exception) {
            logger.error(e) { "Error writing event ${event["type"]}" }
        }
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(COLLECTION_NAME)
}
