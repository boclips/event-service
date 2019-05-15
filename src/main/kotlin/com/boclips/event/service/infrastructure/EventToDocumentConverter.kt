package com.boclips.event.service.infrastructure

import com.boclips.events.types.UserActivated
import com.boclips.events.types.base.CollectionEvent
import com.boclips.events.types.base.Event
import com.boclips.events.types.base.UserEvent
import com.boclips.events.types.collection.*
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideosSearched
import org.bson.Document

object EventToDocumentConverter {

    fun convertUserActivated(userActivated: UserActivated): Document {
        return Document(
                convertUserEvent(userActivated, type = "USER_ACTIVATED")
                        + ("totalUsers" to userActivated.totalUsers)
                        + ("activatedUsers" to userActivated.activatedUsers)
        )
    }

    fun convertVideosSearched(videosSearched: VideosSearched): Document {
        return Document(
                convertUserEvent(videosSearched, type = "VIDEOS_SEARCHED")
                        + ("query" to videosSearched.query)
                        + ("pageIndex" to videosSearched.pageIndex)
                        + ("pageSize" to videosSearched.pageSize)
                        + ("totalResults" to videosSearched.totalResults.toLong())
        )
    }

    fun convertVideoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed): Document {
        return Document(convertUserEvent(videoSegmentPlayed, "VIDEO_SEGMENT_PLAYED")
                + ("segmentStartSeconds" to videoSegmentPlayed.segmentStartSeconds)
                + ("segmentEndSeconds" to videoSegmentPlayed.segmentEndSeconds)
                + ("playerId" to videoSegmentPlayed.playerId)
                + ("videoDurationSeconds" to videoSegmentPlayed.videoDurationSeconds)
                + ("videoIndex" to videoSegmentPlayed.videoIndex)
                + ("videoId" to videoSegmentPlayed.videoId)
        )
    }

    fun convertVideoAddedToCollection(videoAddedToCollection: VideoAddedToCollection): Document {
        return Document(convertUserEvent(videoAddedToCollection, "VIDEO_ADDED_TO_COLLECTION")
                + ("videoId" to videoAddedToCollection.videoId)
                + ("collectionId" to videoAddedToCollection.collectionId)
        )
    }

    fun convertVideoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection): Document {
        return Document(convertUserEvent(videoRemovedFromCollection, "VIDEO_REMOVED_FROM_COLLECTION")
                + ("videoId" to videoRemovedFromCollection.videoId)
                + ("collectionId" to videoRemovedFromCollection.collectionId)
        )
    }

    fun convertCollectionBookmarked(collectionBookmarked: CollectionBookmarked): Document {
        return convertCollectionEvent(collectionBookmarked, "COLLECTION_BOOKMARKED")
    }

    fun convertCollectionUnbookmarked(collectionUnbookmarked: CollectionUnbookmarked): Document {
        return convertCollectionEvent(collectionUnbookmarked, "COLLECTION_UNBOOKMARKED")
    }

    fun convertCollectionMadePrivate(event: CollectionMadePrivate): Document {
        return convertCollectionEvent(event, "COLLECTION_MADE_PRIVATE")
    }

    fun convertCollectionMadePublic(event: CollectionMadePublic): Document {
        return convertCollectionEvent(event, "COLLECTION_MADE_PUBLIC")
    }

    fun convertCollectionSubjectsChanged(event: CollectionSubjectsChanged): Document {
        return convertCollectionEvent(event, "COLLECTION_SUBJECTS_CHANGED").append("subjects", event.subjects.toList())
    }

    fun convertCollectionAgeRangeChanged(event: CollectionAgeRangeChanged): Document {
        return convertCollectionEvent(event, "COLLECTION_AGE_RANGE_CHANGED").append("rangeMin", event.rangeMin).append("rangeMax", event.rangeMax)
    }

    private fun convertCollectionEvent(event: CollectionEvent, type: String): Document {
        return Document(convertUserEvent(event, type) + ("collectionId" to event.collectionId))
    }

    private fun <T> convertUserEvent(event: T, type: String): Map<String, Any> where T : UserEvent, T : Event {
        return mapOf<String, Any>(
                "type" to type,
                "userId" to event.user.id,
                "userIsBoclips" to event.user.email.endsWith("@boclips.com"),
                "timestamp" to event.timestamp,
                "url" to event.url
        )
    }
}
