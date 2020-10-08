package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.contentpackage.ContentPackageDocument
import com.boclips.event.service.domain.ContentPackageRepository
import com.boclips.eventbus.domain.contentpackage.ContentPackage
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import mu.KLogging
import org.bson.Document
import org.litote.kmongo.getCollection

class MongoContentPackageRepository(private val mongoClient: MongoClient) : ContentPackageRepository {
    companion object : KLogging() {
        const val COLLECTION_NAME = "content-packages"
    }

    override fun save(contentPackage: ContentPackage) {
        try {
            val document = contentPackage.toDocument()
            mongoClient
                .getDatabase(DatabaseConstants.DB_NAME)
                .getCollection<ContentPackageDocument>(COLLECTION_NAME)
                .replaceOne(
                    Document("_id", document.id),
                    document,
                    ReplaceOptions().upsert(true)
                )
        } catch (e: Exception) {
            logger.error(e) { "Error writing content package with ID=${contentPackage.id.value}" }
        }
    }
}

fun ContentPackage.toDocument(): ContentPackageDocument =
    ContentPackageDocument.builder()
        .id(id.value)
        .name(name)
        .build()
