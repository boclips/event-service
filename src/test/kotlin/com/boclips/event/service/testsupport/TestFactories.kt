package com.boclips.event.service.testsupport

import com.boclips.events.types.User
import com.boclips.events.types.UserActivated
import com.boclips.events.types.collection.*
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideosSearched

object TestFactories {

    fun createUser(userId: String = "user-1", userEmail: String = "user@example.com"): User {
        return User.builder().id(userId).email(userEmail).build()
    }

    fun createUserActivated(userId: String = "user-1", userEmail: String = "user@example.com"): UserActivated {
        return UserActivated.builder()
                .user(User.builder().id(userId).email(userEmail).build())
                .totalUsers(100)
                .activatedUsers(50)
                .build()
    }

    fun createVideosSearched(pageIndex: Int = 0, pageSize: Int = 10, query: String = "a great video", totalResults: Long = 14, user: User = createUser()): VideosSearched {
        return VideosSearched
                .builder()
                .pageIndex(pageIndex)
                .pageSize(pageSize)
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

    fun createCollectionBookmarked(collectionId: String): CollectionBookmarked {
        return CollectionBookmarked.builder()
                .collectionId(collectionId)
                .user(createUser())
                .build()
    }

    fun createCollectionUnbookmarked(collectionId: String): CollectionUnbookmarked {
        return CollectionUnbookmarked.builder()
                .collectionId(collectionId)
                .user(createUser())
                .build()
    }

    fun createCollectionMadePrivate(collectionId: String): CollectionMadePrivate {
        return CollectionMadePrivate.builder()
                .collectionId(collectionId)
                .user(createUser())
                .build()
    }

    fun createCollectionMadePublic(collectionId: String): CollectionMadePublic {
        return CollectionMadePublic.builder()
                .collectionId(collectionId)
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
