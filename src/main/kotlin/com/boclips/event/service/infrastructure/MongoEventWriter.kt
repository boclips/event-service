package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.EventWriter
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionAgeRangeChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionBookmarkChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionSubjectsChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionVisibilityChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertUserActivated
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideoAddedToCollection
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideoRemovedFromCollection
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideoSegmentPlayed
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertVideosSearched
import com.boclips.events.types.UserActivated
import com.boclips.events.types.collection.*
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideosSearched
import com.mongodb.MongoClient
import org.bson.Document

class MongoEventWriter(private val mongoClient: MongoClient) : EventWriter {
    override fun writeVideosSearched(videosSearched: VideosSearched) {
        write(convertVideosSearched(videosSearched))
    }

    override fun writeUserActivated(userActivated: UserActivated) {
        write(convertUserActivated(userActivated))
    }

    override fun writeVideoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed) {
        write(convertVideoSegmentPlayed(videoSegmentPlayed))
    }

    override fun writeVideoAddedToCollection(videoAddedToCollection: VideoAddedToCollection) {
        write(convertVideoAddedToCollection(videoAddedToCollection))
    }

    override fun writeVideoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection) {
        write(convertVideoRemovedFromCollection(videoRemovedFromCollection))
    }

    override fun writeCollectionBookmarkChanged(collectionBookmarkChanged: CollectionBookmarkChanged) {
        write(convertCollectionBookmarkChanged(collectionBookmarkChanged))
    }

    override fun writeCollectionVisibilityChanged(collectionVisibilityChanged: CollectionVisibilityChanged) {
        write(convertCollectionVisibilityChanged(collectionVisibilityChanged))
    }

    override fun writeCollectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged) {
        write(convertCollectionSubjectsChanged(collectionSubjectsChanged))
    }

    override fun writeCollectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged) {
        write(convertCollectionAgeRangeChanged(collectionAgeRangeChanged))
    }

    private fun write(document: Document) {
        mongoClient.getDatabase("video-service-db").getCollection("event-log").insertOne(document)
    }
}
