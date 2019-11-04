package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.domain.VideoRepository
import com.boclips.eventbus.domain.video.Video
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import mu.KLogging
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.getCollection

class MongoVideoRepository(private val mongoClient: MongoClient) : VideoRepository {
    companion object: KLogging()

    override fun saveVideo(video: Video) {
        val document = VideoDocument(
                id = video.id.value,
                title = video.title,
                contentPartnerName = video.contentPartner.name,
                playbackProviderType = video.playbackProviderType.name,
                subjects = video.subjects.map { it.name }.toSet(),
                ageRangeMin = video.ageRange.min,
                ageRangeMax = video.ageRange.max,
                durationSeconds = video.durationSeconds
        )

        write(video.id.value, document)
    }

    private fun write(id: String, document: VideoDocument) {
        try {
            getCollection().replaceOne(Document("_id", id), document, ReplaceOptions().upsert(true))
        } catch (e: Exception) {
            logger.error(e) { "Error writing video ${document.id}" }
        }
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<VideoDocument>("videos")
}

data class VideoDocument(
        @BsonId
        val id: String,
        val title: String,
        val contentPartnerName: String,
        val playbackProviderType: String,
        val subjects: Set<String>,
        val ageRangeMin: Int?,
        val ageRangeMax: Int?,
        val durationSeconds: Int
)
