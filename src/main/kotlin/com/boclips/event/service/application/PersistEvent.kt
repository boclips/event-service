package com.boclips.event.service.application

import com.boclips.event.service.domain.EventRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.events.collection.CollectionAgeRangeChanged
import com.boclips.eventbus.events.collection.CollectionBookmarkChanged
import com.boclips.eventbus.events.collection.CollectionSubjectsChanged
import com.boclips.eventbus.events.collection.CollectionVisibilityChanged
import com.boclips.eventbus.events.collection.VideoAddedToCollection
import com.boclips.eventbus.events.collection.VideoRemovedFromCollection
import com.boclips.eventbus.events.user.UserActivated
import com.boclips.eventbus.events.video.VideoPlayerInteractedWith
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideoVisited
import com.boclips.eventbus.events.video.VideosSearched

class PersistEvent(private val eventRepository: EventRepository) {

    @BoclipsEventListener
    fun userActivated(userActivated: UserActivated) {
        eventRepository.saveUserActivated(userActivated)
    }

    @BoclipsEventListener
    fun videosSearched(videosSearched: VideosSearched) {
        eventRepository.saveVideosSearched(videosSearched)
    }

    @BoclipsEventListener
    fun videoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed) {
        eventRepository.saveVideoSegmentPlayed(videoSegmentPlayed)
    }

    @BoclipsEventListener
    fun videoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith) {
        eventRepository.saveVideoPlayerInteractedWith(videoPlayerInteractedWith)
    }

    @BoclipsEventListener
    fun videoAddedToCollection(videoAddedToCollection: VideoAddedToCollection) {
        eventRepository.saveVideoAddedToCollection(videoAddedToCollection)
    }

    @BoclipsEventListener
    fun videoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection) {
        eventRepository.saveVideoRemovedFromCollection(videoRemovedFromCollection)
    }

    @BoclipsEventListener
    fun collectionBookmarkChanged(collectionBookmarkChanged: CollectionBookmarkChanged) {
        eventRepository.saveCollectionBookmarkChanged(collectionBookmarkChanged)
    }

    @BoclipsEventListener
    fun collectionMadePrivate(collectionVisibilityChanged: CollectionVisibilityChanged) {
        eventRepository.saveCollectionVisibilityChanged(collectionVisibilityChanged)
    }

    @BoclipsEventListener
    fun collectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged) {
        eventRepository.saveCollectionSubjectsChanged(collectionSubjectsChanged)
    }

    @BoclipsEventListener
    fun collectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged) {
        eventRepository.saveCollectionAgeRangeChanged(collectionAgeRangeChanged)
    }

    @BoclipsEventListener
    fun videoVisited(videoVisited: VideoVisited) {
        eventRepository.saveVideoVisited(videoVisited)
    }
}
