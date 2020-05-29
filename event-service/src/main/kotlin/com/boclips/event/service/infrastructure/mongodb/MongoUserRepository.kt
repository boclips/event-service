package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.user.OrganisationDocument
import com.boclips.event.infrastructure.user.UserDocument
import com.boclips.event.service.domain.UserRepository
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.domain.user.User
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import mu.KLogging
import org.bson.Document
import org.litote.kmongo.getCollection
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

class MongoUserRepository(private val mongoClient: MongoClient) : UserRepository {

    override fun saveUser(user: User) {

        val organisation = user.organisation?.let(this::organisationDocument)

        val document = UserDocument.builder()
            .id(user.id)
            .firstName(user.profile.firstName)
            .lastName(user.profile.lastName)
            .email(user.email)
            .createdAt(user.createdAt.toInstant().atZone(ZoneOffset.UTC).toString())
            .subjects(subjects(user))
            .ages(user.profile.ages)
            .organisation(organisation)
            .role(user.profile.role)
            .boclipsEmployee(user.isBoclipsEmployee)
            .profileSchool(user.profile.school?.let(this::organisationDocument))
            .build()

        write(document)
    }

    private fun write(document: UserDocument) {
        try {
            getCollection().replaceOne(Document("_id", document.id), document, ReplaceOptions().upsert(true))
        } catch (e: Exception) {
            logger.error(e) { "Error writing user ${document.id}" }
        }
    }

    private fun subjects(user: User): List<String> {
        return user.profile.subjects.map { it.name }
    }

    private fun organisationDocument(organisation: Organisation): OrganisationDocument {
        return OrganisationDocument.builder()
            .id(organisation.id)
            .type(organisation.type)
            .name(organisation.name)
            .postcode(organisation.address.postcode)
            .parent(organisation.parent?.let(this::organisationDocument))
            .countryCode(organisation.address.countryCode)
            .tags(organisation.tags.toList())
            .state(organisation.address.state)
            .dealExpiresAt(organisation.deal.expiresAt?.format(ISO_DATE_TIME))
            .billing(organisation.deal.billing)
            .build()
    }

    private fun getCollection() =
        mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<UserDocument>(COLLECTION_NAME)

    companion object : KLogging() {
        const val COLLECTION_NAME = "users"
    }
}