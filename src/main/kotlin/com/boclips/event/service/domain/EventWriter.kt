package com.boclips.event.service.domain

import com.boclips.events.types.UserActivated
import com.boclips.events.types.collection.*
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideosSearched

interface EventWriter {
    fun writeUserActivated(userActivated: UserActivated)
    fun writeVideosSearched(videosSearched: VideosSearched)
    fun writeVideoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed)
    fun writeVideoAddedToCollection(videoAddedToCollection: VideoAddedToCollection)
    fun writeVideoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection)
    fun writeCollectionBookmarked(collectionBookmarked: CollectionBookmarked)
    fun writeCollectionUnbookmarked(collectionUnbookmarked: CollectionUnbookmarked)
    fun writeCollectionMadePrivate(collectionMadePrivate: CollectionMadePrivate)
    fun writeCollectionMadePublic(collectionMadePublic: CollectionMadePublic)
    fun writeCollectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged)
    fun writeCollectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged)
}
