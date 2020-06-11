package com.boclips.event.service.domain

import com.boclips.event.infrastructure.EventFields.COLLECTION_AGE_RANGE_CHANGED_RANGE_MAX
import com.boclips.event.infrastructure.EventFields.COLLECTION_AGE_RANGE_CHANGED_RANGE_MIN
import com.boclips.event.infrastructure.EventFields.COLLECTION_BOOKMARK_CHANGED_IS_BOOKMARKED
import com.boclips.event.infrastructure.EventFields.COLLECTION_ID
import com.boclips.event.infrastructure.EventFields.COLLECTION_SUBJECTS_CHANGED_SUBJECTS
import com.boclips.event.infrastructure.EventFields.COLLECTION_VISIBILITY_CHANGED_IS_DISCOVERABLE
import com.boclips.event.infrastructure.EventFields.DEVICE_ID
import com.boclips.event.infrastructure.EventFields.EXTERNAL_USER_ID
import com.boclips.event.infrastructure.EventFields.ORGANISATION_ID
import com.boclips.event.infrastructure.EventFields.ORGANISATION_PARENT_ID
import com.boclips.event.infrastructure.EventFields.ORGANISATION_PARENT_TYPE
import com.boclips.event.infrastructure.EventFields.ORGANISATION_TYPE
import com.boclips.event.infrastructure.EventFields.PAYLOAD
import com.boclips.event.infrastructure.EventFields.PLAYBACK_SEGMENT_END_SECONDS
import com.boclips.event.infrastructure.EventFields.PLAYBACK_SEGMENT_START_SECONDS
import com.boclips.event.infrastructure.EventFields.PLAYBACK_VIDEO_INDEX
import com.boclips.event.infrastructure.EventFields.PLAYER_INTERACTED_WITH_CURRENT_TIME
import com.boclips.event.infrastructure.EventFields.SEARCH_QUERY
import com.boclips.event.infrastructure.EventFields.SEARCH_RESOURCE_TYPE
import com.boclips.event.infrastructure.EventFields.SEARCH_RESULTS_PAGE_INDEX
import com.boclips.event.infrastructure.EventFields.SEARCH_RESULTS_PAGE_RESOURCE_IDS
import com.boclips.event.infrastructure.EventFields.SEARCH_RESULTS_PAGE_SIZE
import com.boclips.event.infrastructure.EventFields.SEARCH_RESULTS_PAGE_VIDEO_IDS
import com.boclips.event.infrastructure.EventFields.SEARCH_RESULTS_TOTAL
import com.boclips.event.infrastructure.EventFields.SUBTYPE
import com.boclips.event.infrastructure.EventFields.TIMESTAMP
import com.boclips.event.infrastructure.EventFields.TYPE
import com.boclips.event.infrastructure.EventFields.Type
import com.boclips.event.infrastructure.EventFields.URL
import com.boclips.event.infrastructure.EventFields.USER_ID
import com.boclips.event.infrastructure.EventFields.VIDEO_ID
import com.boclips.eventbus.events.base.AbstractCollectionEvent
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
        return convertUserEvent(videosSearched, type = Type.VIDEOS_SEARCHED) +
            (SEARCH_QUERY to videosSearched.query) +
            (SEARCH_RESULTS_PAGE_INDEX to videosSearched.pageIndex) +
            (SEARCH_RESULTS_PAGE_SIZE to videosSearched.pageSize) +
            (SEARCH_RESULTS_PAGE_VIDEO_IDS to videosSearched.pageVideoIds) +
            (SEARCH_RESULTS_TOTAL to videosSearched.totalResults)
    }

    fun convertVideoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed): Map<String, Any> {
        return convertUserEvent(videoSegmentPlayed, Type.VIDEO_SEGMENT_PLAYED) +
            (PLAYBACK_SEGMENT_START_SECONDS to videoSegmentPlayed.segmentStartSeconds) +
            (PLAYBACK_SEGMENT_END_SECONDS to videoSegmentPlayed.segmentEndSeconds) +
            (PLAYBACK_VIDEO_INDEX to videoSegmentPlayed.videoIndex) +
            (VIDEO_ID to videoSegmentPlayed.videoId) +
            (DEVICE_ID to (videoSegmentPlayed.deviceId ?: videoSegmentPlayed.playbackDevice))
    }

    fun convertVideoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith): Map<String, Any> {
        return convertUserEvent(videoPlayerInteractedWith, Type.VIDEO_PLAYER_INTERACTED_WITH) +
            (VIDEO_ID to videoPlayerInteractedWith.videoId) +
            (PLAYER_INTERACTED_WITH_CURRENT_TIME to videoPlayerInteractedWith.currentTime) +
            (SUBTYPE to videoPlayerInteractedWith.subtype) +
            (PAYLOAD to videoPlayerInteractedWith.payload)
    }

    fun convertVideoAddedToCollection(videoAddedToCollection: VideoAddedToCollection): Map<String, Any> {
        return convertUserEvent(videoAddedToCollection, Type.VIDEO_ADDED_TO_COLLECTION) +
            (VIDEO_ID to videoAddedToCollection.videoId) +
            (COLLECTION_ID to videoAddedToCollection.collectionId)
    }

    fun convertVideoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection): Map<String, Any> {
        return convertUserEvent(videoRemovedFromCollection, Type.VIDEO_REMOVED_FROM_COLLECTION) +
            (VIDEO_ID to videoRemovedFromCollection.videoId) +
            (COLLECTION_ID to videoRemovedFromCollection.collectionId)
    }

    fun convertVideoInteractedWith(event: VideoInteractedWith): Map<String, Any> {
        return convertUserEvent(event, Type.VIDEO_INTERACTED_WITH) +
            (VIDEO_ID to event.videoId) +
            (SUBTYPE to event.subtype) +
            (PAYLOAD to event.payload)
    }

    fun convertCollectionBookmarkChanged(event: CollectionBookmarkChanged): Map<String, Any> {
        return convertCollectionEvent(event, Type.COLLECTION_BOOKMARK_CHANGED) +
            (COLLECTION_BOOKMARK_CHANGED_IS_BOOKMARKED to event.isBookmarked)
    }

    fun convertCollectionVisibilityChanged(event: CollectionVisibilityChanged): Map<String, Any> {
        return convertCollectionEvent(event, Type.COLLECTION_VISIBILITY_CHANGED) +
            (COLLECTION_VISIBILITY_CHANGED_IS_DISCOVERABLE to event.isDiscoverable)
    }

    fun convertCollectionSubjectsChanged(event: CollectionSubjectsChanged): Map<String, Any> {
        return convertCollectionEvent(event, Type.COLLECTION_SUBJECTS_CHANGED) +
            (COLLECTION_SUBJECTS_CHANGED_SUBJECTS to event.subjects.toList())
    }

    fun convertCollectionAgeRangeChanged(event: CollectionAgeRangeChanged): Map<String, Any> {
        return convertCollectionEvent(event, Type.COLLECTION_AGE_RANGE_CHANGED) +
            (COLLECTION_AGE_RANGE_CHANGED_RANGE_MIN to event.rangeMin) +
            (COLLECTION_AGE_RANGE_CHANGED_RANGE_MAX to event.rangeMax)
    }

    private fun convertCollectionEvent(event: AbstractCollectionEvent, type: String): Map<String, Any> {
        return convertUserEvent(event, type) + (COLLECTION_ID to event.collectionId)
    }

    fun convertUserEvent(event: AbstractEventWithUserId, type: String): Map<String, Any> {
        return mapOf<String, Any>(
            TYPE to type,
            USER_ID to event.userId,
            EXTERNAL_USER_ID to (event.externalUserId ?: event.overrideUserId),
            DEVICE_ID to event.deviceId,
            TIMESTAMP to Date.from(event.timestamp.toInstant()),
            URL to event.url
        )
    }

    fun convertPageRendered(event: PageRendered): Map<String, Any> {
        return mapOf<String, Any>(
            USER_ID to event.userId,
            TIMESTAMP to Date.from(event.timestamp.toInstant()),
            URL to event.url,
            TYPE to Type.PAGE_RENDERED
        )
    }

    fun convertCollectionInteractedWith(event: CollectionInteractedWith): Map<String, Any> {
        return convertUserEvent(event, Type.COLLECTION_INTERACTED_WITH) +
            (COLLECTION_ID to event.collectionId) +
            (SUBTYPE to event.subtype.toString())
    }

    fun convertUserExpired(event: UserExpired): Map<String, Any> {

        val coreFields = mapOf<String, Any>(
            TYPE to Type.USER_EXPIRED,
            USER_ID to event.user.id,
            TIMESTAMP to Date.from(event.timestamp.toInstant()),
            URL to event.url
        )

        val organisationFields = if (event.user.organisation != null) {
            mapOf(
                ORGANISATION_ID to event.user.organisation.id,
                ORGANISATION_TYPE to event.user.organisation.type
            )
        } else emptyMap()

        val parentOrganisationFields = if (event.user.organisation?.parent != null) {
            mapOf(
                ORGANISATION_PARENT_ID to event.user.organisation.parent.id,
                ORGANISATION_PARENT_TYPE to event.user.organisation.parent.type
            )
        } else emptyMap()

        return coreFields + organisationFields + parentOrganisationFields
    }

    fun convertResourcesSearched(event: ResourcesSearched): Map<String, Any> {
        return convertUserEvent(event, type = Type.RESOURCES_SEARCHED) +
            (SEARCH_QUERY to event.query) +
            (SEARCH_RESULTS_PAGE_INDEX to event.pageIndex) +
            (SEARCH_RESULTS_PAGE_SIZE to event.pageSize) +
            (SEARCH_RESULTS_PAGE_RESOURCE_IDS to event.pageResourceIds) +
            (SEARCH_RESULTS_TOTAL to event.totalResults) +
            (SEARCH_RESOURCE_TYPE to event.resourceType)
    }

    fun convertPlatformInteractedWith(event: PlatformInteractedWith): Map<String, Any> {
        val convertedEvent = mapOf<String, Any>(
            TYPE to Type.PLATFORM_INTERACTED_WITH,
            TIMESTAMP to Date.from(event.timestamp.toInstant()),
            URL to event.url,
            SUBTYPE to event.subtype
        )

        if (event.userId != null) {
            return convertedEvent + (USER_ID to event.userId)
        }

        return convertedEvent
    }
}
