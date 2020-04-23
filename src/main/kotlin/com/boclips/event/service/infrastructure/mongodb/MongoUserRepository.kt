package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.domain.UserRepository
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.domain.user.User
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.MongoClient
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import java.time.ZoneOffset

class MongoUserRepository(private val mongoClient: MongoClient) : UserRepository {

    override fun saveUser(event: UserCreated) {

        val organisation = event.user.organisation?.let(this::organisationDocument)

        val document = UserDocument(
            id = event.user.id,
            firstName = event.user.profile.firstName,
            lastName = event.user.profile.lastName,
            email = event.user.email,
            createdAt = event.timestamp.toInstant().atZone(ZoneOffset.UTC).toString(),
            subjects = subjects(event.user),
            ages = event.user.profile.ages,
            organisation = organisation,
            role = event.user.profile.role,
            isBoclipsEmployee = event.user.isBoclipsEmployee,
            profileSchool = event.user.profile.school?.let(this::organisationDocument)
        )

        getCollection().save(document)
    }

    override fun updateUser(event: UserUpdated) {
        val organisation = event.user.organisation?.let(this::organisationDocument)
        getCollection()
            .updateOne(
                UserDocument::id eq event.user.id,
                set(
                    UserDocument::organisation setTo organisation,
                    UserDocument::firstName setTo event.user.profile.firstName,
                    UserDocument::lastName setTo event.user.profile.lastName,
                    UserDocument::email setTo event.user.email,
                    UserDocument::subjects setTo subjects(event.user),
                    UserDocument::ages setTo event.user.profile.ages,
                    UserDocument::role setTo event.user.profile.role,
                    UserDocument::profileSchool setTo event.user.profile.school?.let(this::organisationDocument)
                )
            )
    }

    private fun subjects(user: User): List<String> {
        return user.profile.subjects.map { it.name }
    }

    private fun organisationDocument(organisation: Organisation): OrganisationDocument {
        return OrganisationDocument(
            id = organisation.id,
            accountType = organisation.accountType,
            type = organisation.type,
            name = organisation.name,
            postcode = organisation.postcode,
            parent = organisation.parent?.let(this::organisationDocument)
        )
    }

    private fun getCollection() =
        mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<UserDocument>(COLLECTION_NAME)

    companion object {
        const val COLLECTION_NAME = "users"
    }
}

data class UserDocument(
    @BsonId
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val subjects: List<String>,
    val ages: List<Int>,
    val createdAt: String,
    val organisation: OrganisationDocument?,
    val profileSchool: OrganisationDocument?,
    val role: String?,
    @param:JsonProperty("isBoclipsEmployee")
    @get:JsonProperty("isBoclipsEmployee")
    val isBoclipsEmployee: Boolean
)

data class OrganisationDocument(
    val id: String,
    val accountType: String,
    val name: String,
    val parent: OrganisationDocument?,
    val type: String,
    val postcode: String?
)
