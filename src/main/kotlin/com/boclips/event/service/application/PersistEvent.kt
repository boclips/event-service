package com.boclips.event.service.application

import com.boclips.event.service.domain.EventRepository
import com.boclips.events.config.Subscriptions.*
import com.boclips.events.types.UserActivated
import com.boclips.events.types.collection.*
import com.boclips.events.types.video.VideoPlayerInteractedWith
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideosSearched
import org.springframework.cloud.stream.annotation.StreamListener

class PersistEvent(private val eventRepository: EventRepository) {

    @StreamListener(USER_ACTIVATED)
    fun userActivated(userActivated: UserActivated) {
        eventRepository.saveUserActivated(userActivated)
    }

    @StreamListener(VIDEOS_SEARCHED)
    fun videosSearched(videosSearched: VideosSearched) {
        eventRepository.saveVideosSearched(videosSearched)
    }

    @StreamListener(VIDEO_SEGMENT_PLAYED)
    fun videoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed) {
        eventRepository.saveVideoSegmentPlayed(videoSegmentPlayed)
    }

    @StreamListener(VIDEO_PLAYER_INTERACTED_WITH)
    fun videoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith) {
        eventRepository.saveVideoPlayerInteractedWith(videoPlayerInteractedWith)
    }

    @StreamListener(VIDEO_ADDED_TO_COLLECTION)
    fun videoAddedToCollection(videoAddedToCollection: VideoAddedToCollection) {
        eventRepository.saveVideoAddedToCollection(videoAddedToCollection)
    }

    @StreamListener(VIDEO_REMOVED_FROM_COLLECTION)
    fun videoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection) {
        eventRepository.saveVideoRemovedFromCollection(videoRemovedFromCollection)
    }

    @StreamListener(COLLECTION_BOOKMARK_CHANGED)
    fun collectionBookmarkChanged(collectionBookmarkChanged: CollectionBookmarkChanged) {
        eventRepository.saveCollectionBookmarkChanged(collectionBookmarkChanged)
    }

    @StreamListener(COLLECTION_VISIBILITY_CHANGED)
    fun collectionMadePrivate(collectionVisibilityChanged: CollectionVisibilityChanged) {
        eventRepository.saveCollectionVisibilityChanged(collectionVisibilityChanged)
    }

    @StreamListener(COLLECTION_SUBJECTS_CHANGED)
    fun collectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged) {
        eventRepository.saveCollectionSubjectsChanged(collectionSubjectsChanged)
    }

    @StreamListener(COLLECTION_AGE_RANGE_CHANGED)
    fun collectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged) {
        eventRepository.saveCollectionAgeRangeChanged(collectionAgeRangeChanged)
    }

}
