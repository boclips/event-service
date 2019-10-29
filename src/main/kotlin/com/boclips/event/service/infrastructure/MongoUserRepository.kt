package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.UserRepository
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.domain.user.User
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.mongodb.MongoClient
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.*
import java.time.ZoneOffset

class MongoUserRepository(private val mongoClient: MongoClient) : UserRepository {

    override fun saveUser(event: UserCreated) {

        val organisation = event.user.organisation?.let(this::organisationDocument)

        val document = UserDocument(
                id = event.user.id,
                firstName = event.user.firstName,
                lastName = event.user.lastName,
                email = event.user.email,
                createdAt = event.timestamp.toInstant().atZone(ZoneOffset.UTC).toString(),
                subjects = subjects(event.user),
                organisation = organisation,
                isBoclipsEmployee = event.user.isBoclipsEmployee
        )

        getCollection().save(document)
    }

    override fun updateUser(event: UserUpdated) {
        val organisation = event.user.organisation?.let(this::organisationDocument)
        getCollection()
                .updateOne(
                        UserDocument::id eq event.user.id,
                        set(
                            UserDocument::organisation.setTo(organisation),
                            UserDocument::firstName.setTo(event.user.firstName),
                            UserDocument::lastName.setTo(event.user.lastName),
                            UserDocument::email.setTo(event.user.email),
                            UserDocument::subjects.setTo(subjects(event.user))
                        )
                )
    }

    private fun subjects(user: User): List<String> {
        return user.subjects.map { it.name }
    }

    private fun organisationDocument(organisation: Organisation): OrganisationDocument {
        return OrganisationDocument(id = organisation.id, type = organisation.type, name = organisation.name,postcode = organisation.postcode,
                parent = organisation.parent?.let(this::organisationDocument))
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<UserDocument>(COLLECTION)

    companion object {
        const val COLLECTION = "users"
    }
}

data class UserDocument (
        @BsonId
        val id: String,
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val subjects: List<String>,
        val createdAt: String,
        val organisation: OrganisationDocument?,
        val isBoclipsEmployee: Boolean
)

data class OrganisationDocument (
        val id: String,
        val name: String,
        val parent: OrganisationDocument?,
        val type: String,
        val postcode: String?
)
