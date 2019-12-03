package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.domain.CollectionRepository
import com.boclips.eventbus.domain.collection.Collection
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import mu.KLogging
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.getCollection
import org.litote.kmongo.set
import java.util.*

class MongoCollectionRepository(private val mongoClient: MongoClient) : CollectionRepository {

    companion object: KLogging() {
        const val COLLECTION_NAME = "collections"
    }

    override fun saveCollection(collection: Collection) {
        val document = CollectionDocument(
                id = collection.id.value,
                title = collection.title,
                description = collection.description,
                subjects = collection.subjects.map { it.name },
                minAge = collection.ageRange.min,
                maxAge = collection.ageRange.max,
                videoIds = collection.videosIds.map { it.value },
                ownerId = collection.ownerId.value,
                bookmarks = collection.bookmarks.map { it.value },
                createdTime = Date.from(collection.createdAt.toInstant()),
                updatedTime = Date.from(collection.updatedAt.toInstant()),
                isPublic = collection.isPublic,
                isDeleted = false
        )

        withCollection { it.replaceOne(idEquals(collection.id.value), document, ReplaceOptions().upsert(true)) }
    }

    override fun markDeleted(collectionId: String) {
        withCollection { it.updateOne(idEquals(collectionId), set(CollectionDocument::isDeleted, true)) }
    }

    private fun idEquals(id: String) = Document("_id", id)

    private fun withCollection(consumer: (MongoCollection<CollectionDocument>) -> Unit) {
        val collection = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<CollectionDocument>(COLLECTION_NAME)
        try {
            consumer(collection)
        } catch (e: Exception) {
            logger.error(e) { "Error writing collection" }
        }
    }

}

data class CollectionDocument(
        @BsonId
        val id: String,
        val title: String,
        val description: String,
        val subjects: List<String>,
        val minAge: Int?,
        val maxAge: Int?,
        val videoIds: List<String>,
        val ownerId: String,
        val bookmarks: List<String>,
        val createdTime: Date,
        val updatedTime: Date,
        val isPublic: Boolean,
        val isDeleted: Boolean
)
