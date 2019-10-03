package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.UserRepository
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.MongoClient
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.eq
import org.litote.kmongo.save
import java.time.ZoneOffset
import org.litote.kmongo.getCollection
import org.litote.kmongo.set

class MongoUserRepository(private val mongoClient: MongoClient) : UserRepository {

    override fun saveUser(event: UserCreated) {

        val organisation = event.organisation?.let(this::organisationDocument)

        val document = UserDocument(
                id = event.user.id,
                createdAt = event.timestamp.toInstant().atZone(ZoneOffset.UTC).toString(),
                organisation = organisation,
                isBoclipsEmployee = event.user.isBoclipsEmployee
        )

        getCollection().save(document)
    }

    override fun updateUser(event: UserUpdated) {
        val organisation = event.organisation ?: return
        getCollection()
                .updateOne(
                        UserDocument::id eq event.userId,
                        set(UserDocument::organisation, organisationDocument(organisation))
                )
    }

    private fun organisationDocument(organisation: Organisation): OrganisationDocument {
        return OrganisationDocument(id = organisation.id, type = organisation.type)
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<UserDocument>(COLLECTION)

    companion object {
        const val COLLECTION = "users"
    }
}

data class UserDocument (
        @BsonId
        val id: String,
        val createdAt: String,
        val organisation: OrganisationDocument?,
        val isBoclipsEmployee: Boolean
)

data class OrganisationDocument (
        val id: String,
        val type: String
)
