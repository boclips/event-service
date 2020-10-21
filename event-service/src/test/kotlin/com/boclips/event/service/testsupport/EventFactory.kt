package com.boclips.event.service.testsupport

import com.boclips.event.service.testsupport.UserFactory.createUser
import com.boclips.eventbus.domain.ResourceType
import com.boclips.eventbus.domain.user.User
import com.boclips.eventbus.events.collection.CollectionAgeRangeChanged
import com.boclips.eventbus.events.collection.CollectionBookmarkChanged
import com.boclips.eventbus.events.collection.CollectionInteractedWith
import com.boclips.eventbus.events.collection.CollectionInteractionType
import com.boclips.eventbus.events.collection.CollectionSubjectsChanged
import com.boclips.eventbus.events.collection.CollectionVisibilityChanged
import com.boclips.eventbus.events.collection.VideoAddedToCollection
import com.boclips.eventbus.events.collection.VideoRemovedFromCollection
import com.boclips.eventbus.events.page.PageRendered
import com.boclips.eventbus.events.platform.PlatformInteractedWith
import com.boclips.eventbus.events.resource.ResourcesSearched
import com.boclips.eventbus.events.searchsuggestions.SearchQueryCompletionsSuggested
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.eventbus.events.video.VideoInteractedWith
import com.boclips.eventbus.events.video.VideoPlayerInteractedWith
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideosSearched
import java.time.ZonedDateTime

object EventFactory {

    fun createUserCreated(
        user: User = createUser(),
        timestamp: ZonedDateTime = ZonedDateTime.now()
    ): UserCreated {
        return UserCreated
            .builder()
            .user(user)
            .timestamp(timestamp)
            .build()
    }

    fun createUserUpdated(user: User, timestamp: ZonedDateTime = ZonedDateTime.now()): UserUpdated {
        return UserUpdated.builder()
            .user(user)
            .timestamp(timestamp)
            .build()
    }

    fun createVideoInteractedWith(
        timestamp: ZonedDateTime = ZonedDateTime.now(),
        videoId: String = "video-id",
        subtype: String = "share-to-google-classroom",
        payload: Map<String, Any> = HashMap(),
        user: User = createUser(),
        url: String? = "https://example.com"
    ): VideoInteractedWith {
        return VideoInteractedWith.builder()
            .timestamp(timestamp)
            .videoId(videoId)
            .subtype(subtype)
            .payload(payload)
            .userId(user.id)
            .url(url)
            .build()
    }

    fun createCollectionInteractedWith(
        timestamp: ZonedDateTime = ZonedDateTime.now(),
        collectionId: String = "collection-default-id",
        subtype: CollectionInteractionType = CollectionInteractionType.NAVIGATE_TO_COLLECTION_DETAILS,
        user: User = createUser(),
        url: String? = "https://example.com"
    ): CollectionInteractedWith {
        return CollectionInteractedWith.builder()
            .timestamp(timestamp)
            .collectionId(collectionId)
            .subtype(subtype)
            .userId(user.id)
            .url(url)
            .build()
    }

    fun createVideosSearched(
        pageIndex: Int = 0,
        pageSize: Int = 10,
        query: String = "a great video",
        totalResults: Long = 14,
        user: User = createUser()
    ): VideosSearched {
        return VideosSearched
            .builder()
            .pageIndex(pageIndex)
            .pageSize(pageSize)
            .pageVideoIds(emptyList())
            .query(query)
            .totalResults(totalResults)
            .userId(user.id)
            .build()
    }

    fun createVideoSegmentPlayed(
        segmentEndSeconds: Long = 10,
        segmentStartSeconds: Long = 5,
        videoId: String = "123",
        videoIndex: Int = 1,
        query: String? = null,
        user: User = createUser()
    ): VideoSegmentPlayed {
        return VideoSegmentPlayed
            .builder()
            .segmentEndSeconds(segmentEndSeconds)
            .segmentStartSeconds(segmentStartSeconds)
            .videoId(videoId)
            .videoIndex(videoIndex)
            .query(query)
            .userId(user.id)
            .build()
    }

    fun createVideoPlayerInteractedWith(
        user: User = createUser(),
        videoId: String = "123",
        currentTime: Long = 55,
        subtype: String = "captions-on",
        payload: Map<String, Any> = mapOf(Pair("id", "caption-id"))
    ): VideoPlayerInteractedWith {
        return VideoPlayerInteractedWith
            .builder()
            .userId(user.id)
            .videoId(videoId)
            .currentTime(currentTime)
            .subtype(subtype)
            .payload(payload)
            .build()
    }

    fun createVideoAddedToCollection(videoId: String, collectionId: String): VideoAddedToCollection {
        val user = createUser()
        return VideoAddedToCollection
            .builder()
            .videoId(videoId)
            .collectionId(collectionId)
            .userId(user.id)
            .build()
    }

    fun createVideoRemovedFromCollection(videoId: String, collectionId: String): VideoRemovedFromCollection {
        val user = createUser()
        return VideoRemovedFromCollection
            .builder()
            .videoId(videoId)
            .collectionId(collectionId)
            .userId(user.id)
            .build()
    }

    fun createCollectionBookmarkChanged(
        collectionId: String = "collection-id",
        isBookmarked: Boolean = true,
        user: User = createUser()
    ): CollectionBookmarkChanged {
        return CollectionBookmarkChanged
            .builder()
            .collectionId(collectionId)
            .userId(user.id)
            .isBookmarked(isBookmarked)
            .build()
    }

    fun createCollectionVisibilityChanged(collectionId: String, isDiscoverable: Boolean): CollectionVisibilityChanged {
        val user = createUser()
        return CollectionVisibilityChanged
            .builder()
            .collectionId(collectionId)
            .isDiscoverable(isDiscoverable)
            .userId(user.id)
            .build()
    }

    fun createCollectionSubjectsChanged(collectionId: String, subjects: Set<String>): CollectionSubjectsChanged {
        val user = createUser()
        return CollectionSubjectsChanged
            .builder()
            .collectionId(collectionId)
            .userId(user.id)
            .subjects(subjects)
            .build()
    }

    fun createCollectionAgeRangeChanged(
        collectionId: String,
        rangeMin: Int,
        rangeMax: Int?
    ): CollectionAgeRangeChanged {
        val user = createUser()
        return CollectionAgeRangeChanged
            .builder()
            .collectionId(collectionId)
            .userId(user.id)
            .rangeMin(rangeMin)
            .rangeMax(rangeMax)
            .build()
    }

    fun createPageRendered(
        userId: String = "user-12",
        url: String = "http://bbc.co.uk",
        timestamp: ZonedDateTime = ZonedDateTime.now()
    ): PageRendered {
        return PageRendered.builder()
            .userId(userId)
            .url(url)
            .timestamp(timestamp)
            .build()
    }

    fun createPlatformInteractedWith(
        subtype: String,
        userId: String = "user-12",
        url: String = "http://bbc.co.uk",
        timestamp: ZonedDateTime = ZonedDateTime.now()
    ): PlatformInteractedWith {
        return PlatformInteractedWith.builder()
            .subtype(subtype)
            .userId(userId)
            .url(url)
            .timestamp(timestamp)
            .build()
    }

    fun createPlatformInteractedWithAnonymous(
        subtype: String,
        url: String = "http://bbc.co.uk",
        timestamp: ZonedDateTime = ZonedDateTime.now()
    ): PlatformInteractedWith {
        return PlatformInteractedWith.builder()
            .subtype(subtype)
            .url(url)
            .timestamp(timestamp)
            .build()
    }

    fun createResourcesSearched(
        userId: String = "happy-user",
        url: String = "http://bbc.co.uk",
        query: String = "sharks",
        resourceType: ResourceType = ResourceType.COLLECTION,
        pageIndex: Int = 1,
        pageSize: Int = 20,
        totalResults: Long = 400,
        pageResourceIds: List<String> = listOf("id3", "id2"),
        timestamp: ZonedDateTime = ZonedDateTime.now()
    ): ResourcesSearched {
        return ResourcesSearched.builder()
            .userId(userId)
            .url(url)
            .query(query)
            .resourceType(resourceType)
            .pageIndex(pageIndex)
            .pageSize(pageSize)
            .totalResults(totalResults)
            .pageResourceIds(pageResourceIds)
            .timestamp(timestamp).build()
    }

    fun createSearchQueryCompletionsSuggested(
            userId: String = "happy-user",
            url: String = "http://bbc.co.uk",
            searchQuery: String = "bio",
            completionId: String = "completion-id",
            impressions: List<String> = listOf("biology", "biodiversity"),
            componentId: String = "component-id",
            timestamp: ZonedDateTime = ZonedDateTime.now()
    ): SearchQueryCompletionsSuggested {
        return SearchQueryCompletionsSuggested.builder()
                .userId(userId)
                .url(url)
                .searchQuery(searchQuery)
                .completionId(completionId)
                .impressions(impressions)
                .componentId(componentId)
                .timestamp(timestamp)
                .build()
    }
}
