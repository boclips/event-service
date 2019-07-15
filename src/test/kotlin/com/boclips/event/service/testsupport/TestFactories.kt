package com.boclips.event.service.testsupport

import com.boclips.events.types.User
import com.boclips.events.types.UserActivated
import com.boclips.events.types.collection.*
import com.boclips.events.types.video.VideoPlayerInteractedWith
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideoUpdated
import com.boclips.events.types.video.VideosSearched

object TestFactories {

    fun createVideoUpdates(videoId: String, title: String, contentPartnerName: String): VideoUpdated {
        return VideoUpdated.builder()
            .videoId(videoId)
            .title(title)
            .contentPartnerName(contentPartnerName)
            .build()
    }

    fun createUser(userId: String = "user-1", isBoclipsEmployee: Boolean = false): User {
        return User.builder().id(userId).isBoclipsEmployee(isBoclipsEmployee).build()
    }

    fun createUserActivated(userId: String = "user-1", isBoclipsEmployee: Boolean = false): UserActivated {
        return UserActivated.builder()
                .user(User.builder().id(userId).isBoclipsEmployee(isBoclipsEmployee).build())
                .totalUsers(100)
                .activatedUsers(50)
                .build()
    }

    fun createVideosSearched(
            pageIndex: Int = 0,
            pageSize: Int = 10,
            pageVideoIds: List<String> = emptyList(),
            query: String = "a great video",
            totalResults: Long = 14,
            user: User = createUser()
    ): VideosSearched {
        return VideosSearched
                .builder()
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .pageVideoIds(pageVideoIds)
                .query(query)
                .totalResults(totalResults)
                .user(user)
                .build()
    }

    fun createVideoSegmentPlayed(segmentEndSeconds: Long = 10, segmentStartSeconds: Long = 5, videoDurationSeconds: Long = 100, videoId: String = "123", videoIndex: Int = 1, playerId: String = "1", user: User = createUser()): VideoSegmentPlayed {
        return VideoSegmentPlayed
                .builder()
                .segmentEndSeconds(segmentEndSeconds)
                .segmentStartSeconds(segmentStartSeconds)
                .videoDurationSeconds(videoDurationSeconds)
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
            videoDurationSeconds: Long = 100,
            currentTime: Long = 55,
            subtype: String = "captions-on",
            payload: Map<String, Any> = mapOf(Pair("id", "caption-id"))
    ): VideoPlayerInteractedWith {
        return VideoPlayerInteractedWith
                .builder()
                .user(user)
                .videoId(videoId)
                .playerId(playerId)
                .videoDurationSeconds(videoDurationSeconds)
                .currentTime(currentTime)
                .subtype(subtype)
                .payload(payload)
                .build()
    }

    fun createVideoAddedToCollection(videoId: String, collectionId: String): VideoAddedToCollection {
        return VideoAddedToCollection.builder()
                .videoId(videoId)
                .collectionId(collectionId)
                .user(createUser())
                .build()
    }

    fun createVideoRemovedFromCollection(videoId: String, collectionId: String): VideoRemovedFromCollection {
        return VideoRemovedFromCollection.builder()
                .videoId(videoId)
                .collectionId(collectionId)
                .user(createUser())
                .build()
    }

    fun createCollectionBookmarkChanged(collectionId: String, isBookmarked: Boolean): CollectionBookmarkChanged {
        return CollectionBookmarkChanged.builder()
                .collectionId(collectionId)
                .user(createUser())
                .isBookmarked(isBookmarked)
                .build()
    }

    fun createCollectionVisibilityChanged(collectionId: String, isPublic: Boolean): CollectionVisibilityChanged {
        return CollectionVisibilityChanged.builder()
                .collectionId(collectionId)
                .isPublic(isPublic)
                .user(createUser())
                .build()
    }

    fun createCollectionSubjectsChanged(collectionId: String, subjects: Set<String>): CollectionSubjectsChanged {
        return CollectionSubjectsChanged.builder()
                .collectionId(collectionId)
                .user(createUser())
                .subjects(subjects)
                .build()
    }

    fun createCollectionAgeRangeChanged(collectionId: String, rangeMin: Int, rangeMax: Int?): CollectionAgeRangeChanged {
        return CollectionAgeRangeChanged.builder()
                .collectionId(collectionId)
                .user(createUser())
                .rangeMin(rangeMin)
                .rangeMax(rangeMax)
                .build()
    }
}
