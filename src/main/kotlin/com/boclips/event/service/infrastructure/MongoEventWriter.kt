package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.EventWriter
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertUserActivated
import com.boclips.events.types.UserActivated
import com.mongodb.MongoClient
import org.bson.Document

class MongoEventWriter(private val mongoClient: MongoClient) : EventWriter {

    override fun writeUserActivated(userActivated: UserActivated) {
        write(convertUserActivated(userActivated))
    }

    private fun write(document: Document) {
        mongoClient.getDatabase("video-service-db").getCollection("event-log").insertOne(document)
    }
}
