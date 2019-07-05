package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.VideoRepository
import com.mongodb.InsertOptions
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.litote.kmongo.updateOne

class MongoVideoRepository(private val mongoClient: MongoClient) : VideoRepository {

    override fun saveVideo(id: String, title: String, contentPartnerName: String) {
        val document = Document()
            .append("_id", id)
            .append("title", title)
            .append("contentPartnerName", contentPartnerName)
        write(id, document)
    }

    private fun write(id: String, document: Document) {
        try {
            getCollection().replaceOne(Document("_id", id), document, ReplaceOptions().upsert(true))
        }
        catch(e: Exception) {
            MongoEventRepository.logger.error(e) { "Error writing video ${document["id"]}" }
        }
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection("videos")
}
