package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.domain.user.User
import com.boclips.eventbus.domain.video.ContentPartner
import com.boclips.eventbus.domain.video.PlaybackProviderType
import com.boclips.eventbus.domain.video.Video
import com.boclips.eventbus.domain.video.VideoId
import com.boclips.eventbus.events.collection.CollectionAgeRangeChanged
import com.boclips.eventbus.events.collection.CollectionBookmarkChanged
import com.boclips.eventbus.events.collection.CollectionSubjectsChanged
import com.boclips.eventbus.events.collection.CollectionVisibilityChanged
import com.boclips.eventbus.events.collection.VideoAddedToCollection
import com.boclips.eventbus.events.collection.VideoRemovedFromCollection
import com.boclips.eventbus.events.user.UserCreated
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
        durationSeconds: Int = 180
    ): Video {
        return Video
            .builder()
            .id(VideoId(id))
            .title(title)
            .contentPartner(ContentPartner.of(contentPartnerName))
            .playbackProviderType(playbackProviderType)
            .subjects(subjectNames.map {
                Subject
                    .builder()
                    .id(SubjectId("id-$it"))
                    .name(it)
                    .build()
            })
            .ageRange(ageRange)
            .durationSeconds(durationSeconds)
            .build()
    }

    fun createUser(
        userId: String = "user-1",
        isBoclipsEmployee: Boolean = false,
        organisationId: String = "organisation-id"
    ): User {
        return User
            .builder()
            .id(userId)
            .isBoclipsEmployee(isBoclipsEmployee)
            .organisationId(organisationId)
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
            .user(user)
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
            .user(user)
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
            .user(user)
            .videoId(videoId)
            .playerId(playerId)
            .currentTime(currentTime)
            .subtype(subtype)
            .payload(payload)
            .build()
    }

    fun createVideoAddedToCollection(videoId: String, collectionId: String): VideoAddedToCollection {
        return VideoAddedToCollection
            .builder()
            .videoId(videoId)
            .collectionId(collectionId)
            .user(createUser())
            .build()
    }

    fun createVideoRemovedFromCollection(videoId: String, collectionId: String): VideoRemovedFromCollection {
        return VideoRemovedFromCollection
            .builder()
            .videoId(videoId)
            .collectionId(collectionId)
            .user(createUser())
            .build()
    }

    fun createCollectionBookmarkChanged(collectionId: String = "collection-id", isBookmarked: Boolean = true, user: User = createUser()): CollectionBookmarkChanged {
        return CollectionBookmarkChanged
            .builder()
            .collectionId(collectionId)
            .user(user)
            .isBookmarked(isBookmarked)
            .build()
    }

    fun createCollectionVisibilityChanged(collectionId: String, isPublic: Boolean): CollectionVisibilityChanged {
        return CollectionVisibilityChanged
            .builder()
            .collectionId(collectionId)
            .isPublic(isPublic)
            .user(createUser())
            .build()
    }

    fun createCollectionSubjectsChanged(collectionId: String, subjects: Set<String>): CollectionSubjectsChanged {
        return CollectionSubjectsChanged
            .builder()
            .collectionId(collectionId)
            .user(createUser())
            .subjects(subjects)
            .build()
    }

    fun createCollectionAgeRangeChanged(
        collectionId: String,
        rangeMin: Int,
        rangeMax: Int?
    ): CollectionAgeRangeChanged {
        return CollectionAgeRangeChanged
            .builder()
            .collectionId(collectionId)
            .user(createUser())
            .rangeMin(rangeMin)
            .rangeMax(rangeMax)
            .build()
    }

    fun createUserCreated(
        userId: String = "user-id",
        organisationId: String = "organisation-id",
        isBoclipsEmployee: Boolean = false,
        timestamp: ZonedDateTime = ZonedDateTime.now()
    ): UserCreated {
        return UserCreated.builder()
            .user(
                User.builder()
                    .id(userId)
                    .organisationId(organisationId)
                    .isBoclipsEmployee(isBoclipsEmployee)
                    .build()
            )
            .timestamp(Date.from(timestamp.toInstant()))
            .build()
    }
}
