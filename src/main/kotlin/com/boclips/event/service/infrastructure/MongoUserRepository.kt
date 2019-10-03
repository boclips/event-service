package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.UserRepository
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import org.bson.Document
import org.litote.kmongo.save
import java.time.ZoneOffset

class MongoUserRepository(private val mongoClient: MongoClient) : UserRepository {
    override fun saveUser(event: UserCreated) {

        val organisation = event.organisation?.let(this::organisationDocument)

        val document = Document()
            .append("_id", event.user.id)
            .append("createdAt", event.timestamp.toInstant().atZone(ZoneOffset.UTC).toString())
            .append("organisation", organisation)
            .append("isBoclipsEmployee", event.user.isBoclipsEmployee)
        getCollection().save(document)
    }

    override fun updateUser(event: UserUpdated) {
        val organisation = event.organisation ?: return
        getCollection()
                .updateOne(BasicDBObject().append("_id", event.userId), BasicDBObject().append("\$set", BasicDBObject().append("organisation", organisationDocument(organisation))))
    }

    private fun organisationDocument(organisation: Organisation): BasicDBObject {
        return BasicDBObject()
                .append("id", organisation.id)
                .append("type", organisation.type)
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(COLLECTION)

    companion object {
        const val COLLECTION = "users"
    }
}
