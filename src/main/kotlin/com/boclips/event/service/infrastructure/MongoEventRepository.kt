package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.EventRepository
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionAgeRangeChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionBookmarkChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionSubjectsChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionVisibilityChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertUserActivated
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideoAddedToCollection
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideoPlayerInteractedWith
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideoRemovedFromCollection
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideoSegmentPlayed
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideoVisited
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideosSearched
import com.boclips.eventbus.events.collection.*
import com.boclips.eventbus.events.user.UserActivated
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.video.VideoPlayerInteractedWith
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideoVisited
import com.boclips.eventbus.events.video.VideosSearched
import com.mongodb.MongoClient
import mu.KLogging
import org.bson.Document

class MongoEventRepository(private val mongoClient: MongoClient) : EventRepository {
    companion object: KLogging()

    override fun saveVideosSearched(videosSearched: VideosSearched) {
        write(convertVideosSearched(videosSearched))
    }

    override fun saveUserActivated(userActivated: UserActivated) {
        write(convertUserActivated(userActivated))
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

    override fun saveVideoVisited(videoVisited: VideoVisited) {
        write(convertVideoVisited(videoVisited))
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

    private fun write(document: Document) {
        try {
            getCollection().insertOne(document)
        }
        catch(e: Exception) {
            logger.error(e) { "Error writing event ${document["type"]}" }
        }
    }

    private fun getCollection() = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection("events")
}
