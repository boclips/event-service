package com.boclips.event.service.domain

import com.boclips.events.types.UserActivated
import com.boclips.events.types.collection.*
import com.boclips.events.types.video.VideoPlayerInteractedWith
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideosSearched

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
