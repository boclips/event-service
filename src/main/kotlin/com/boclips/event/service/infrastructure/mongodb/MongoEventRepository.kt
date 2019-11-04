package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.domain.EventRepository
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertCollectionAgeRangeChanged
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertCollectionBookmarkChanged
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertCollectionSubjectsChanged
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertCollectionVisibilityChanged
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertVideoAddedToCollection
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertVideoInteractedWith
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertVideoPlayerInteractedWith
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertVideoRemovedFromCollection
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertVideoSegmentPlayed
import com.boclips.event.service.infrastructure.EventToEntityConverter.convertVideosSearched
import com.boclips.eventbus.events.collection.*
import com.boclips.eventbus.events.video.VideoInteractedWith
import com.boclips.eventbus.events.video.VideoPlayerInteractedWith
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideosSearched
import com.mongodb.MongoClient
import mu.KLogging
import org.bson.Document

class MongoEventRepository(private val mongoClient: MongoClient) : EventRepository {
    companion object: KLogging()

    override fun saveVideosSearched(videosSearched: VideosSearched) {
        write(convertVideosSearched(videosSearched))
    }

    override fun saveVideoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed) {
        write(convertVideoSegmentPlayed(videoSegmentPlayed))
    }

    override fun saveVideoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith) {
        write(convertVideoPlayerInteractedWith(videoPlayerInteractedWith))
    }

    override fun saveVideoAddedToCollection(videoAddedToCollection: VideoAddedToCollection) {
        write(convertVideoAddedToCollection(videoAddedToCollection))
    }

    override fun saveVideoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection) {
        write(convertVideoRemovedFromCollection(videoRemovedFromCollection))
    }

    override fun saveCollectionBookmarkChanged(collectionBookmarkChanged: CollectionBookmarkChanged) {
        write(convertCollectionBookmarkChanged(collectionBookmarkChanged))
    }

    override fun saveCollectionVisibilityChanged(collectionVisibilityChanged: CollectionVisibilityChanged) {
        write(convertCollectionVisibilityChanged(collectionVisibilityChanged))
    }

    override fun saveCollectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged) {
        write(convertCollectionSubjectsChanged(collectionSubjectsChanged))
    }

    override fun saveCollectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged) {
        write(convertCollectionAgeRangeChanged(collectionAgeRangeChanged))
    }

    override fun saveVideoInteractedWith(event: VideoInteractedWith) {
        write(convertVideoInteractedWith(event))
    }

    private fun write(fields: Map<String, Any>) {
        try {
            getCollection().insertOne(Document(fields))
        }
        catch(e: Exception) {
            logger.error(e) { "Error writing event ${fields["type"]}" }
        }
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection("events")
}
