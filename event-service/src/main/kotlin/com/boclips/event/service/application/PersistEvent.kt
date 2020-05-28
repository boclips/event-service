package com.boclips.event.service.application

import com.boclips.event.service.domain.EventRepository
import com.boclips.eventbus.BoclipsEventListener
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

class PersistEvent(private val eventRepository: EventRepository) {

    @BoclipsEventListener
    fun resourcesSearched(resourcesSearched: ResourcesSearched) {
        eventRepository.saveResourcesSearched(resourcesSearched)
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
    fun collectionInteractedWith(event: CollectionInteractedWith) {
        eventRepository.saveCollectionInteractedWith(event)
    }

    @BoclipsEventListener
    fun videoInteractedWith(event: VideoInteractedWith) {
        eventRepository.saveVideoInteractedWith(event)
    }

    @BoclipsEventListener
    fun pageRendered(event: PageRendered) {
        eventRepository.savePageRendered(event)
    }

    @BoclipsEventListener
    fun platformInteractedWith(event: PlatformInteractedWith) {
        eventRepository.savePlatformInteractedWith(event)
    }

    @BoclipsEventListener
    fun userExpired(event: UserExpired) {
        eventRepository.saveUserExpired(event)
    }
}
