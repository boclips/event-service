package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.channel.CategoryWithAncestorsDocument
import com.boclips.event.infrastructure.video.VideoAssetDocument
import com.boclips.event.infrastructure.video.VideoDocument
import com.boclips.event.infrastructure.video.VideoTopicDocument
import com.boclips.event.service.domain.VideoRepository
import com.boclips.eventbus.domain.category.CategoryWithAncestors
import com.boclips.eventbus.domain.video.Video
import com.boclips.eventbus.domain.video.VideoAsset
import com.boclips.eventbus.domain.video.VideoCategorySource
import com.boclips.eventbus.domain.video.VideoTopic
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
            .description(video.description)
            .channelId(video.channelId.value)
            .playbackProviderType(video.playbackProviderType.name)
            .playbackId(video.playbackId)
            .subjects(video.subjects.map { it.name }.toSet())
            .ageRangeMin(video.ageRange.min)
            .ageRangeMax(video.ageRange.max)
            .durationSeconds(video.durationSeconds)
            .type(video.type.name)
            .originalHeight(video.originalDimensions?.height)
            .originalWidth(video.originalDimensions?.width)
            .assets(convertVideoAssetToVideoAssetDocument(video.assets))
            .promoted(video.promoted)
            .topics(video.topics.map(this::topicToDocument))
            .keywords(video.keywords)
            .sourceVideoReference(video.sourceVideoReference)
            .deactivated(video.deactivated)
            .categories(toCategoriesDocument(video.categories))
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

    private fun toCategoriesDocument(categories: Map<VideoCategorySource, Set<CategoryWithAncestors>>): Map<String, Set<CategoryWithAncestorsDocument>> {
        return categories.map {
            it.key.name to (it.value.map { it.toDocument() }.toSet())
        }.toMap()
    }

    fun CategoryWithAncestors.toDocument(): CategoryWithAncestorsDocument =
        CategoryWithAncestorsDocument.builder()
            .code(code)
            .description(description)
            .ancestors(ancestors)
            .build()

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

    private fun topicToDocument(
        topic: VideoTopic
    ): VideoTopicDocument {
        val topicChain: MutableList<VideoTopic> = mutableListOf()
        var currentTopic: VideoTopic? = topic
        while (currentTopic != null) {
            topicChain.add(currentTopic)
            currentTopic = currentTopic.parent
        }
        val convertNext = { thisTopic: VideoTopic, parent: VideoTopicDocument? ->
            VideoTopicDocument.builder()
                .name(thisTopic.name)
                .confidence(thisTopic.confidence)
                .language(thisTopic.language.toLanguageTag())
                .parent(parent)
                .build()
        }
        return topicChain
            .dropLast(1)
            .foldRight(
                initial = convertNext(topicChain.last(), null),
                operation = convertNext
            )
    }
}

