package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.EventWriter
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionAgeRangeChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionBookmarked
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionMadePrivate
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionMadePublic
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionSubjectsChanged
import com.boclips.event.service.infrastructure.EventToDocumentConverter.convertCollectionUnbookmarked
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

    override fun writeCollectionBookmarked(collectionBookmarked: CollectionBookmarked) {
        write(convertCollectionBookmarked(collectionBookmarked))
    }

    override fun writeCollectionUnbookmarked(collectionUnbookmarked: CollectionUnbookmarked) {
        write(convertCollectionUnbookmarked(collectionUnbookmarked))
    }

    override fun writeCollectionMadePrivate(collectionMadePrivate: CollectionMadePrivate) {
        write(convertCollectionMadePrivate(collectionMadePrivate))
    }

    override fun writeCollectionMadePublic(collectionMadePublic: CollectionMadePublic) {
        write(convertCollectionMadePublic(collectionMadePublic))
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
