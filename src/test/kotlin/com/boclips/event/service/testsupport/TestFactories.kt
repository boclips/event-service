package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.domain.collection.Collection
import com.boclips.eventbus.domain.collection.CollectionId
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.domain.user.User
import com.boclips.eventbus.domain.user.UserId
import com.boclips.eventbus.domain.video.*
import com.boclips.eventbus.events.collection.CollectionAgeRangeChanged
import com.boclips.eventbus.events.collection.CollectionBookmarkChanged
import com.boclips.eventbus.events.collection.CollectionInteractedWith
import com.boclips.eventbus.events.collection.CollectionInteractionType
import com.boclips.eventbus.events.collection.CollectionSubjectsChanged
import com.boclips.eventbus.events.collection.CollectionVisibilityChanged
import com.boclips.eventbus.events.collection.VideoAddedToCollection
import com.boclips.eventbus.events.collection.VideoRemovedFromCollection
import com.boclips.eventbus.events.order.Order
import com.boclips.eventbus.events.page.PageRendered
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.eventbus.events.video.VideoInteractedWith
import com.boclips.eventbus.events.video.VideoPlayerInteractedWith
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideosSearched
import java.time.ZonedDateTime
import java.util.Date

object TestFactories {

    fun createVideo(
        id: String = "",
        title: String = "",
        contentPartnerName: String = "",
        playbackProviderType: PlaybackProviderType = PlaybackProviderType.KALTURA,
        subjectNames: List<String> = emptyList(),
        ageRange: AgeRange = AgeRange(),
        durationSeconds: Int = 180,
        type: VideoType? = null
    ): Video {
        return Video
            .builder()
            .id(VideoId(id))
            .title(title)
            .contentPartner(ContentPartner.of(contentPartnerName))
            .playbackProviderType(playbackProviderType)
            .subjects(subjectsFromNames(subjectNames))
            .ageRange(ageRange)
            .durationSeconds(durationSeconds)
            .type(type)
            .build()
    }

    fun createCollection(
            id: String = "",
            title: String = "",
            description: String = "",
            subjects: Set<String> = emptySet(),
            ageRange: AgeRange = AgeRange(null, null),
            videoIds: List<String> = emptyList(),
            ownerId: String = "",
            createdTime: ZonedDateTime = ZonedDateTime.now(),
            updatedTime: ZonedDateTime = ZonedDateTime.now(),
            bookmarks: List<String> = emptyList(),
            isPublic: Boolean = true
    ): Collection {
        return Collection.builder()
                .id(CollectionId(id))
                .createdTime(Date.from(createdTime.toInstant()))
                .updatedTime(Date.from(updatedTime.toInstant()))
                .title(title)
                .description(description)
                .subjects(subjects.map { Subject(SubjectId("$it-id"), it) })
                .ageRange(ageRange)
                .videosIds(videoIds.map { VideoId(it) })
                .ownerId(UserId(ownerId))
                .bookmarks(bookmarks.map { UserId(it) })
                .isPublic(isPublic)
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
            .timestamp(Date.from(timestamp.toInstant()))
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
            .timestamp(Date.from(timestamp.toInstant()))
            .collectionId(collectionId)
            .subtype(subtype)
            .userId(user.id)
            .url(url)
            .build()
    }

    fun createUser(
        userId: String = "user-1",
        organisation: Organisation? = null,
        subjectNames: List<String> = emptyList(),
        ages: List<Int> = emptyList(),
        isBoclipsEmployee: Boolean = false
    ): User {
        return User
            .builder()
            .id(userId)
            .isBoclipsEmployee(isBoclipsEmployee)
            .organisation(organisation)
            .subjects(subjectsFromNames(subjectNames))
            .ages(ages)
            .build()
    }

    fun createOrganisation(
            id: String = "organisation-id",
            accountType: String = "DESIGN_PARTNER",
            name: String = "organisation-name",
            postcode: String = "post-code",
            parent: Organisation? = null,
            type: String = "API"
    ): Organisation {
        return Organisation
                .builder()
                .id(id)
                .accountType(accountType)
                .type(type)
                .name(name)
                .postcode(postcode)
                .parent(parent)
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
        playerId: String = "1",
        user: User = createUser()
    ): VideoSegmentPlayed {
        return VideoSegmentPlayed
            .builder()
            .segmentEndSeconds(segmentEndSeconds)
            .segmentStartSeconds(segmentStartSeconds)
            .videoId(videoId)
            .videoIndex(videoIndex)
            .playerId(playerId)
            .userId(user.id)
            .build()
    }

    fun createVideoPlayerInteractedWith(
        user: User = createUser(),
        videoId: String = "123",
        playerId: String = "1",
        currentTime: Long = 55,
        subtype: String = "captions-on",
        payload: Map<String, Any> = mapOf(Pair("id", "caption-id"))
    ): VideoPlayerInteractedWith {
        return VideoPlayerInteractedWith
            .builder()
            .userId(user.id)
            .videoId(videoId)
            .playerId(playerId)
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

    fun createCollectionBookmarkChanged(collectionId: String = "collection-id", isBookmarked: Boolean = true, user: User = createUser()): CollectionBookmarkChanged {
        return CollectionBookmarkChanged
            .builder()
            .collectionId(collectionId)
            .userId(user.id)
            .isBookmarked(isBookmarked)
            .build()
    }

    fun createCollectionVisibilityChanged(collectionId: String, isPublic: Boolean): CollectionVisibilityChanged {
        val user = createUser()
        return CollectionVisibilityChanged
            .builder()
            .collectionId(collectionId)
            .isPublic(isPublic)
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

    fun createUserCreated(
        userId: String = "user-id",
        firstName: String = "first-name",
        lastName: String = "last-name",
        email: String = "email@example.com",
        organisation: Organisation? = createOrganisation(),
        isBoclipsEmployee: Boolean = false,
        subjectNames: List<String> = emptyList(),
        ages: List<Int> = emptyList(),
        timestamp: ZonedDateTime = ZonedDateTime.now()
    ): UserCreated {
        return UserCreated.builder()
            .user(
                User.builder()
                    .id(userId)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .subjects(subjectsFromNames(subjectNames))
                    .ages(ages)
                    .isBoclipsEmployee(isBoclipsEmployee)
                    .organisation(organisation)
                    .build()
            )
            .timestamp(Date.from(timestamp.toInstant()))
            .build()
    }

    fun createUserUpdated(
        userId: String = "user-id",
        firstName: String = "first-name",
        lastName: String = "last-name",
        email: String = "email@example.com",
        organisation: Organisation? = createOrganisation(),
        isBoclipsEmployee: Boolean = false,
        subjectNames: List<String> = emptyList(),
        ages: List<Int> = emptyList(),
        timestamp: ZonedDateTime = ZonedDateTime.now()
    ): UserUpdated {
        return UserUpdated.builder()
            .user(
                User.builder()
                    .id(userId)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .subjects(subjectsFromNames(subjectNames))
                    .ages(ages)
                    .isBoclipsEmployee(isBoclipsEmployee)
                    .organisation(organisation)
                    .build()
            )
            .timestamp(Date.from(timestamp.toInstant()))
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
            .timestamp(Date.from(timestamp.toInstant()))
            .build()
    }

    fun createOrder(
            id: String = "order-123",
            createdAt: ZonedDateTime = ZonedDateTime.now(),
            updatedAt: ZonedDateTime = ZonedDateTime.now(),
            videosIds: List<String> = emptyList()
    ): Order {
        return Order.builder()
                .id(id)
                .dateCreated(Date.from(createdAt.toInstant()))
                .dateUpdated(Date.from(updatedAt.toInstant()))
                .videoIds(videosIds.map { VideoId(it) })
                .build()
    }

    private fun subjectsFromNames(subjectNames: List<String>): List<Subject> {
        return subjectNames.map {
            Subject
                    .builder()
                    .id(SubjectId("id-$it"))
                    .name(it)
                    .build()
        }
    }

}
