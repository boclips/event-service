package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.video.VideoAssetDocument
import com.boclips.event.infrastructure.video.VideoDocument
import com.boclips.event.service.domain.VideoRepository
import com.boclips.eventbus.domain.video.Video
import com.boclips.eventbus.domain.video.VideoAsset
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import mu.KLogging
import org.bson.Document
import org.litote.kmongo.getCollection
import java.time.format.DateTimeFormatter.ISO_DATE
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

class MongoVideoRepository(private val mongoClient: MongoClient) : VideoRepository {
    companion object : KLogging() {
        const val COLLECTION_NAME = "videos"
    }

    override fun saveVideo(video: Video) {
        val document = VideoDocument.builder()
            .id(video.id.value)
            .ingestedAt(video.ingestedAt.withFixedOffsetZone().format(ISO_DATE_TIME))
            .releasedOn(video.releasedOn.format(ISO_DATE))
            .title(video.title)
            .channelId(video.channelId.value)
            .playbackProviderType(video.playbackProviderType.name)
            .subjects(video.subjects.map { it.name }.toSet())
            .ageRangeMin(video.ageRange.min)
            .ageRangeMax(video.ageRange.max)
            .durationSeconds(video.durationSeconds)
            .type(video.type.name)
            .originalHeight(video.originalDimensions?.height)
            .originalWidth(video.originalDimensions?.width)
            .assets(convertVideoAssetToVideoAssetDocument(video.assets))
            .promoted(video.promoted)
            .build()

        write(document)
    }

    private fun write(document: VideoDocument) {
        try {
            getCollection().replaceOne(Document("_id", document.id), document, ReplaceOptions().upsert(true))
        } catch (e: Exception) {
            logger.error(e) { "Error writing video ${document.id}" }
        }
    }

    private fun getCollection() =
        mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<VideoDocument>(
            COLLECTION_NAME
        )

    private fun convertVideoAssetToVideoAssetDocument(assets: List<VideoAsset>?): List<VideoAssetDocument>? {
        return assets?.map {
            VideoAssetDocument.builder()
                .id(it.id)
                .width(it.dimensions.width)
                .height(it.dimensions.height)
                .bitrateKbps(it.bitrateKbps)
                .sizeKb(it.sizeKb)
                .build()

        }
    }
}

