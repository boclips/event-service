package com.boclips.event.service.domain

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

class EventRepository(private val writer: EventWriter) {

    fun saveVideosSearched(videosSearched: VideosSearched) {
        writer.write(EventSerializer.convertVideosSearched(videosSearched))
    }

    fun saveVideoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed) {
        writer.write(EventSerializer.convertVideoSegmentPlayed(videoSegmentPlayed))
    }

    fun saveVideoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith) {
        writer.write(EventSerializer.convertVideoPlayerInteractedWith(videoPlayerInteractedWith))
    }

    fun saveVideoAddedToCollection(videoAddedToCollection: VideoAddedToCollection) {
        writer.write(EventSerializer.convertVideoAddedToCollection(videoAddedToCollection))
    }

    fun saveVideoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection) {
        writer.write(EventSerializer.convertVideoRemovedFromCollection(videoRemovedFromCollection))
    }

    fun saveCollectionBookmarkChanged(collectionBookmarkChanged: CollectionBookmarkChanged) {
        writer.write(EventSerializer.convertCollectionBookmarkChanged(collectionBookmarkChanged))
    }

    fun saveCollectionVisibilityChanged(collectionVisibilityChanged: CollectionVisibilityChanged) {
        writer.write(EventSerializer.convertCollectionVisibilityChanged(collectionVisibilityChanged))
    }

    fun saveCollectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged) {
        writer.write(EventSerializer.convertCollectionSubjectsChanged(collectionSubjectsChanged))
    }

    fun saveCollectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged) {
        writer.write(EventSerializer.convertCollectionAgeRangeChanged(collectionAgeRangeChanged))
    }

    fun saveVideoInteractedWith(event: VideoInteractedWith) {
        writer.write(EventSerializer.convertVideoInteractedWith(event))
    }

    fun savePageRendered(event: PageRendered){
        writer.write(EventSerializer.convertPageRendered(event))
    }

    fun saveCollectionInteractedWith(event: CollectionInteractedWith) {
        writer.write(EventSerializer.convertCollectionInteractedWith(event))
    }

    fun saveUserExpired(event: UserExpired) {
        writer.write(EventSerializer.convertUserExpired(event))
    }

    fun saveResourcesSearched(event: ResourcesSearched) {
        writer.write(EventSerializer.convertResourcesSearched(event))
    }

    fun savePlatformInteractedWith(event: PlatformInteractedWith) {
        writer.write(EventSerializer.convertPlatformInteractedWith(event))
    }
}
