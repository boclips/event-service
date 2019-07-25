package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.VideoRepository
import com.boclips.eventbus.domain.video.Video
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import org.bson.Document

class MongoVideoRepository(private val mongoClient: MongoClient) : VideoRepository {

    override fun saveVideo(video: Video) {
        val document = Document()
            .append("_id", video.id.value)
            .append("title", video.title)
            .append("contentPartnerName", video.contentPartner.name)
            .append("subjects", video.subjects.map {
                it
                    .name
            })
            .append("ageRangeMin", video.ageRange.min)
            .append("ageRangeMax", video.ageRange.max)
        write(video.id.value, document)
    }

    private fun write(id: String, document: Document) {
        try {
            getCollection()
                .replaceOne(Document("_id", id), document, ReplaceOptions().upsert(true))
        } catch (e: Exception) {
            MongoEventRepository
                .logger
                .error(e) { "Error writing video ${document["id"]}" }
        }
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection("videos")
}
