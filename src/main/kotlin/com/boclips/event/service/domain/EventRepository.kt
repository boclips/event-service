package com.boclips.event.service.domain

import com.boclips.eventbus.events.collection.CollectionAgeRangeChanged
import com.boclips.eventbus.events.collection.CollectionBookmarkChanged
import com.boclips.eventbus.events.collection.CollectionSubjectsChanged
import com.boclips.eventbus.events.collection.CollectionVisibilityChanged
import com.boclips.eventbus.events.collection.VideoAddedToCollection
import com.boclips.eventbus.events.collection.VideoRemovedFromCollection
import com.boclips.eventbus.events.video.VideoInteractedWith
import com.boclips.eventbus.events.video.VideoPlayerInteractedWith
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideosSearched

interface EventRepository {
    fun saveVideosSearched(videosSearched: VideosSearched)
    fun saveVideoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed)
    fun saveVideoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith)
    fun saveVideoAddedToCollection(videoAddedToCollection: VideoAddedToCollection)
    fun saveVideoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection)
    fun saveCollectionBookmarkChanged(collectionBookmarkChanged: CollectionBookmarkChanged)
    fun saveCollectionVisibilityChanged(collectionVisibilityChanged: CollectionVisibilityChanged)
    fun saveCollectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged)
    fun saveCollectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged)
    fun saveVideoInteractedWith(event: VideoInteractedWith)
}
