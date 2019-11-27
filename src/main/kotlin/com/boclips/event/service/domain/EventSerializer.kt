package com.boclips.event.service.domain

import com.boclips.eventbus.events.base.AbstractCollectionEvent
import com.boclips.eventbus.events.base.AbstractEventWithUserId
import com.boclips.eventbus.events.collection.*
import com.boclips.eventbus.events.page.PageRendered
import com.boclips.eventbus.events.video.VideoInteractedWith
import com.boclips.eventbus.events.video.VideoPlayerInteractedWith
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideosSearched

object EventSerializer {

    fun convertVideosSearched(videosSearched: VideosSearched): Map<String, Any> {
        return convertUserEvent(videosSearched, type = "VIDEOS_SEARCHED") +
                ("query" to videosSearched.query) +
                ("pageIndex" to videosSearched.pageIndex) +
                ("pageSize" to videosSearched.pageSize) +
                ("pageVideoIds" to videosSearched.pageVideoIds) +
                ("totalResults" to videosSearched.totalResults)
    }

    fun convertVideoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed): Map<String, Any> {
        return convertUserEvent(videoSegmentPlayed, "VIDEO_SEGMENT_PLAYED") +
                ("segmentStartSeconds" to videoSegmentPlayed.segmentStartSeconds) +
                ("segmentEndSeconds" to videoSegmentPlayed.segmentEndSeconds) +
                ("playerId" to videoSegmentPlayed.playerId) +
                ("videoIndex" to videoSegmentPlayed.videoIndex) +
                ("videoId" to videoSegmentPlayed.videoId) +
                ("playbackDevice" to videoSegmentPlayed.playbackDevice)
    }

    fun convertVideoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith): Map<String, Any> {
        return convertUserEvent(videoPlayerInteractedWith, "VIDEO_PLAYER_INTERACTED_WITH") +
                ("playerId" to videoPlayerInteractedWith.playerId) +
                ("videoId" to videoPlayerInteractedWith.videoId) +
                ("currentTime" to videoPlayerInteractedWith.currentTime) +
                ("subtype" to videoPlayerInteractedWith.subtype) +
                ("payload" to videoPlayerInteractedWith.payload)
    }

    fun convertVideoAddedToCollection(videoAddedToCollection: VideoAddedToCollection): Map<String, Any> {
        return convertUserEvent(videoAddedToCollection, "VIDEO_ADDED_TO_COLLECTION") +
                ("videoId" to videoAddedToCollection.videoId) +
                ("collectionId" to videoAddedToCollection.collectionId)
    }

    fun convertVideoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection): Map<String, Any> {
        return convertUserEvent(videoRemovedFromCollection, "VIDEO_REMOVED_FROM_COLLECTION") +
                ("videoId" to videoRemovedFromCollection.videoId) +
                ("collectionId" to videoRemovedFromCollection.collectionId)
    }

    fun convertVideoInteractedWith(event: VideoInteractedWith): Map<String, Any> {
        return convertUserEvent(event, "VIDEO_INTERACTED_WITH") +
                ("videoId" to event.videoId) +
                ("subtype" to event.subtype) +
                ("payload" to event.payload)
    }

    fun convertCollectionBookmarkChanged(event: CollectionBookmarkChanged): Map<String, Any> {
        return convertCollectionEvent(event, "COLLECTION_BOOKMARK_CHANGED") +
                ("isBookmarked" to event.isBookmarked)
    }

    fun convertCollectionVisibilityChanged(event: CollectionVisibilityChanged): Map<String, Any> {
        return convertCollectionEvent(event, "COLLECTION_VISIBILITY_CHANGED") +
                ("isPublic" to event.isPublic)
    }

    fun convertCollectionSubjectsChanged(event: CollectionSubjectsChanged): Map<String, Any> {
        return convertCollectionEvent(event, "COLLECTION_SUBJECTS_CHANGED") +
                ("subjects" to event.subjects.toList())
    }

    fun convertCollectionAgeRangeChanged(event: CollectionAgeRangeChanged): Map<String, Any> {
        return convertCollectionEvent(event, "COLLECTION_AGE_RANGE_CHANGED") +
                ("rangeMin" to event.rangeMin) +
                ("rangeMax" to event.rangeMax)
    }

    private fun convertCollectionEvent(event: AbstractCollectionEvent, type: String): Map<String, Any> {
        return convertUserEvent(event, type) + ("collectionId" to event.collectionId)
    }

    private fun convertUserEvent(event: AbstractEventWithUserId, type: String): Map<String, Any> {
        return mapOf<String, Any>(
                "type" to type,
                "userId" to event.userId,
                "timestamp" to event.timestamp,
                "url" to event.url
        )
    }

    fun convertPageRendered(event: PageRendered): Map<String, Any> {
        return mapOf<String,Any>(
            "userId" to event.userId,
            "timestamp" to event.timestamp,
            "url" to event.url,
            "type" to "PAGE_RENDERED"
        )
    }

    fun convertCollectionInteractedWith(event: CollectionInteractedWith): Map<String, Any> {
        return convertUserEvent(event, "COLLECTION_INTERACTED_WITH") +
            ("collectionId" to event.collectionId) +
            ("subtype" to event.subtype)
    }
}
