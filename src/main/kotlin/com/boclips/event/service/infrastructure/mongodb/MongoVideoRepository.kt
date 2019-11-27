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
    companion object: KLogging() {
        const val COLLECTION_NAME = "videos"
    }

    override fun saveVideo(video: Video) {
        val document = VideoDocument(
                id = video.id.value,
                title = video.title,
                contentPartnerName = video.contentPartner.name,
                playbackProviderType = video.playbackProviderType.name,
                subjects = video.subjects.map { it.name }.toSet(),
                ageRangeMin = video.ageRange.min,
                ageRangeMax = video.ageRange.max,
                durationSeconds = video.durationSeconds,
                type = video.type?.name
        )

        write(document)
    }

    private fun write(document: VideoDocument) {
        try {
            getCollection().replaceOne(Document("_id", document.id), document, ReplaceOptions().upsert(true))
        } catch (e: Exception) {
            logger.error(e) { "Error writing video ${document.id}" }
        }
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<VideoDocument>(COLLECTION_NAME)
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
        val durationSeconds: Int,
        val type:  String?
)
