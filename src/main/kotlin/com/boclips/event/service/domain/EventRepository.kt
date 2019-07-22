package com.boclips.event.service.domain

import com.boclips.eventbus.events.collection.*
import com.boclips.eventbus.events.user.UserActivated
import com.boclips.eventbus.events.video.VideoPlayerInteractedWith
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideosSearched

interface EventRepository {
    fun saveUserActivated(userActivated: UserActivated)
    fun saveVideosSearched(videosSearched: VideosSearched)
    fun saveVideoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed)
    fun saveVideoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith)
    fun saveVideoAddedToCollection(videoAddedToCollection: VideoAddedToCollection)
    fun saveVideoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection)
    fun saveCollectionBookmarkChanged(collectionBookmarkChanged: CollectionBookmarkChanged)
    fun saveCollectionVisibilityChanged(collectionVisibilityChanged: CollectionVisibilityChanged)
    fun saveCollectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged)
    fun saveCollectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged)
}
