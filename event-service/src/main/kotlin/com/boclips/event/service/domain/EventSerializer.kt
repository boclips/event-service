package com.boclips.event.service.domain

import com.boclips.eventbus.events.base.AbstractCollectionEvent
import com.boclips.eventbus.events.base.AbstractEvent
import com.boclips.eventbus.events.base.AbstractEventWithUser
import com.boclips.eventbus.events.base.AbstractEventWithUserId
import com.boclips.eventbus.events.collection.CollectionAgeRangeChanged
import com.boclips.eventbus.events.collection.CollectionBookmarkChanged
import com.boclips.eventbus.events.collection.CollectionInteractedWith
import com.boclips.eventbus.events.collection.CollectionSubjectsChanged
import com.boclips.eventbus.events.collection.CollectionVisibilityChanged
import com.boclips.eventbus.events.collection.VideoAddedToCollection
import com.boclips.eventbus.events.collection.VideoRemovedFromCollection
import com.boclips.eventbus.events.page.PageRendered
import com.boclips.eventbus.events.platform.PlatformInteractedWith
import com.boclips.eventbus.events.resource.ResourcesSearched
import com.boclips.eventbus.events.user.UserExpired
import com.boclips.eventbus.events.video.VideoInteractedWith
import com.boclips.eventbus.events.video.VideoPlayerInteractedWith
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideosSearched
import java.util.Date

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
            ("videoIndex" to videoSegmentPlayed.videoIndex) +
            ("videoId" to videoSegmentPlayed.videoId) +
            ("playbackDevice" to videoSegmentPlayed.playbackDevice)
    }

    fun convertVideoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith): Map<String, Any> {
        return convertUserEvent(videoPlayerInteractedWith, "VIDEO_PLAYER_INTERACTED_WITH") +
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
            ("isDiscoverable" to event.isDiscoverable)
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

    fun convertUserEvent(event: AbstractEventWithUserId, type: String): Map<String, Any> {
        return mapOf<String, Any>(
            "type" to type,
            "userId" to event.userId,
            "overrideUserId" to event.overrideUserId,
            "timestamp" to Date.from(event.timestamp.toInstant()),
            "url" to event.url
        )
    }

    fun convertAnonymousEvent(event: AbstractEvent, type: String): Map<String, Any> {
        return mapOf<String, Any>(
            "type" to type,
            "timestamp" to Date.from(event.timestamp.toInstant()),
            "url" to event.url
        )
    }

    private fun convertUserEvent(event: AbstractEventWithUser, type: String): Map<String, Any> {
        return mapOf<String, Any>(
            "type" to type,
            "userId" to event.user.id,
            "timestamp" to Date.from(event.timestamp.toInstant()),
            "url" to event.url
        )
    }

    fun convertPageRendered(event: PageRendered): Map<String, Any> {
        return mapOf<String, Any>(
            "userId" to event.userId,
            "timestamp" to Date.from(event.timestamp.toInstant()),
            "url" to event.url,
            "type" to "PAGE_RENDERED"
        )
    }

    fun convertCollectionInteractedWith(event: CollectionInteractedWith): Map<String, Any> {
        return convertUserEvent(event, "COLLECTION_INTERACTED_WITH") +
            ("collectionId" to event.collectionId) +
            ("subtype" to event.subtype.toString())
    }

    fun convertUserExpired(event: UserExpired): Map<String, Any> {
        return convertUserEvent(event, "USER_EXPIRED").let {
            if (event.user.organisation == null) {
                return@let it
            }

            var extra = mapOf(
                ("organisationId" to event.user.organisation.id),
                ("organisationType" to event.user.organisation.type)
            )

            if (event.user.organisation.parent != null) {
                extra = extra +
                    ("organisationParentId" to event.user.organisation.parent.id) +
                    ("organisationParentType" to event.user.organisation.parent.type)
            }
            return@let it + extra
        }
    }

    fun convertResourcesSearched(event: ResourcesSearched): Map<String, Any> {
        return convertUserEvent(event, type = "RESOURCES_SEARCHED") +
            ("query" to event.query) +
            ("pageIndex" to event.pageIndex) +
            ("pageSize" to event.pageSize) +
            ("pageResourceIds" to event.pageResourceIds) +
            ("totalResults" to event.totalResults) +
            ("resourceType" to event.resourceType)
    }

    fun convertPlatformInteractedWith(event: PlatformInteractedWith): Map<String, Any> {
        val convertedEvent = mapOf<String, Any>(
            "type" to "PLATFORM_INTERACTED_WITH",
            "timestamp" to Date.from(event.timestamp.toInstant()),
            "url" to event.url,
            "subtype" to event.subtype)

        if (event.userId != null) {
            return convertedEvent + ("userId" to event.userId)
        }

        return convertedEvent
    }
}
