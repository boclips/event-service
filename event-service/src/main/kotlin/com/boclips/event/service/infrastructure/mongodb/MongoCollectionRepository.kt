package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.collection.CollectionDocument
import com.boclips.event.service.domain.CollectionRepository
import com.boclips.eventbus.domain.collection.Collection
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.Updates.set
import mu.KLogging
import org.bson.Document
import org.litote.kmongo.getCollection
import java.util.Date

class MongoCollectionRepository(private val mongoClient: MongoClient) : CollectionRepository {

    companion object : KLogging() {
        const val COLLECTION_NAME = "collections"
    }

    override fun saveCollection(collection: Collection) {
        val document = CollectionDocument.builder()
            .id(collection.id.value)
            .title(collection.title)
            .description(collection.description)
            .subjects(collection.subjects.map { it.name })
            .minAge(collection.ageRange.min)
            .maxAge(collection.ageRange.max)
            .videoIds(collection.videosIds.map { it.value })
            .ownerId(collection.ownerId.value)
            .bookmarks(collection.bookmarks.map { it.value })
            .createdTime(Date.from(collection.createdAt.toInstant()))
            .updatedTime(Date.from(collection.updatedAt.toInstant()))
            .discoverable(collection.isDiscoverable)
            .deleted(false)
            .build()

        write(document)
    }

    private fun write(document: CollectionDocument) {
        try {
            getCollection().replaceOne(Document("_id", document.id), document, ReplaceOptions().upsert(true))
        } catch (e: Exception) {
            logger.error(e) { "Error writing collection ${document.id}" }
        }
    }

    override fun markDeleted(collectionId: String) {
        try {
            getCollection().updateOne(Document("_id", collectionId), set("deleted", true))
        } catch (e: Exception) {
            logger.error(e) { "Error marketing deleted for collection $collectionId" }
        }
    }

    private fun getCollection() =
        mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<CollectionDocument>(
            COLLECTION_NAME
        )
}
