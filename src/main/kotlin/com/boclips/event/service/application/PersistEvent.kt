package com.boclips.event.service.application

import com.boclips.event.service.domain.EventRepository
import com.boclips.events.config.subscriptions.*
import com.boclips.events.types.UserActivated
import com.boclips.events.types.collection.*
import com.boclips.events.types.video.VideoPlayerInteractedWith
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideosSearched
import org.springframework.cloud.stream.annotation.StreamListener

class PersistEvent(private val eventRepository: EventRepository) {

    @StreamListener(UserActivatedSubscription.CHANNEL)
    fun userActivated(userActivated: UserActivated) {
        eventRepository.saveUserActivated(userActivated)
    }

    @StreamListener(VideosSearchedSubscription.CHANNEL)
    fun videosSearched(videosSearched: VideosSearched) {
        eventRepository.saveVideosSearched(videosSearched)
    }

    @StreamListener(VideoSegmentPlayedSubscription.CHANNEL)
    fun videoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed) {
        eventRepository.saveVideoSegmentPlayed(videoSegmentPlayed)
    }

    @StreamListener(VideoPlayerInteractedWithSubscription.CHANNEL)
    fun videoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith) {
        eventRepository.saveVideoPlayerInteractedWith(videoPlayerInteractedWith)
    }

    @StreamListener(VideoAddedToCollectionSubscription.CHANNEL)
    fun videoAddedToCollection(videoAddedToCollection: VideoAddedToCollection) {
        eventRepository.saveVideoAddedToCollection(videoAddedToCollection)
    }

    @StreamListener(VideoRemovedFromCollectionSubscription.CHANNEL)
    fun videoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection) {
        eventRepository.saveVideoRemovedFromCollection(videoRemovedFromCollection)
    }

    @StreamListener(CollectionBookmarkChangedSubscription.CHANNEL)
    fun collectionBookmarkChanged(collectionBookmarkChanged: CollectionBookmarkChanged) {
        eventRepository.saveCollectionBookmarkChanged(collectionBookmarkChanged)
    }

    @StreamListener(CollectionVisibilityChangedSubscription.CHANNEL)
    fun collectionMadePrivate(collectionVisibilityChanged: CollectionVisibilityChanged) {
        eventRepository.saveCollectionVisibilityChanged(collectionVisibilityChanged)
    }

    @StreamListener(CollectionSubjectsChangedSubscription.CHANNEL)
    fun collectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged) {
        eventRepository.saveCollectionSubjectsChanged(collectionSubjectsChanged)
    }

    @StreamListener(CollectionAgeRangeChangedSubscription.CHANNEL)
    fun collectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged) {
        eventRepository.saveCollectionAgeRangeChanged(collectionAgeRangeChanged)
    }

}
