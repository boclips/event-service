package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.UserRepository
import com.boclips.eventbus.events.user.UserCreated
import com.mongodb.MongoClient
import org.bson.Document
import org.litote.kmongo.save
import java.time.ZoneOffset

class MongoUserRepository(private val mongoClient: MongoClient) : UserRepository {

    override fun saveUser(event: UserCreated) {
        val document = Document()
            .append("_id", event.user.id)
            .append("createdAt", event.timestamp.toInstant().atZone(ZoneOffset.UTC).toString())
            .append("organisationId", event.organisation?.id)
            .append("isBoclipsEmployee", event.user.isBoclipsEmployee)
        getCollection().save(document)
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(COLLECTION)

    companion object {
        const val COLLECTION = "users"
    }
}
