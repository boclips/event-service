package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.domain.UserRepository
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.domain.user.User
import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.MongoClient
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import java.time.ZoneOffset

class MongoUserRepository(private val mongoClient: MongoClient) : UserRepository {

    override fun saveUser(user: User) {

        val organisation = user.organisation?.let(this::organisationDocument)

        val document = UserDocument(
            id = user.id,
            firstName = user.profile.firstName,
            lastName = user.profile.lastName,
            email = user.email,
            createdAt = user.createdAt.toInstant().atZone(ZoneOffset.UTC).toString(),
            subjects = subjects(user),
            ages = user.profile.ages,
            organisation = organisation,
            role = user.profile.role,
            isBoclipsEmployee = user.isBoclipsEmployee,
            profileSchool = user.profile.school?.let(this::organisationDocument)
        )

        getCollection().save(document)
    }

    private fun subjects(user: User): List<String> {
        return user.profile.subjects.map { it.name }
    }

    private fun organisationDocument(organisation: Organisation): OrganisationDocument {
        return OrganisationDocument(
            id = organisation.id,
            type = organisation.type,
            name = organisation.name,
            postcode = organisation.postcode,
            parent = organisation.parent?.let(this::organisationDocument),
            countryCode = organisation.countryCode,
            tags = organisation.tags,
            state = organisation.state
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
    val name: String,
    val parent: OrganisationDocument?,
    val type: String,
    val tags: Set<String>,
    val postcode: String?,
    val countryCode: String?,
    val state: String?
)
