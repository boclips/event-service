package com.boclips.event.service.application

import com.boclips.event.service.domain.EventWriter
import com.boclips.events.config.Subscriptions.*
import com.boclips.events.types.UserActivated
import com.boclips.events.types.collection.*
import com.boclips.events.types.video.VideoPlayerInteractedWith
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideosSearched
import org.springframework.cloud.stream.annotation.StreamListener

class PersistEvent(private val eventWriter: EventWriter) {

    @StreamListener(USER_ACTIVATED)
    fun userActivated(userActivated: UserActivated) {
        eventWriter.writeUserActivated(userActivated)
    }

    @StreamListener(VIDEOS_SEARCHED)
    fun videosSearched(videosSearched: VideosSearched) {
        eventWriter.writeVideosSearched(videosSearched)
    }

    @StreamListener(VIDEO_SEGMENT_PLAYED)
    fun videoSegmentPlayed(videoSegmentPlayed: VideoSegmentPlayed) {
        eventWriter.writeVideoSegmentPlayed(videoSegmentPlayed)
    }

    @StreamListener(VIDEO_PLAYER_INTERACTED_WITH)
    fun videoPlayerInteractedWith(videoPlayerInteractedWith: VideoPlayerInteractedWith) {
        eventWriter.writeVideoPlayerInteractedWith(videoPlayerInteractedWith)
    }

    @StreamListener(VIDEO_ADDED_TO_COLLECTION)
    fun videoAddedToCollection(videoAddedToCollection: VideoAddedToCollection) {
        eventWriter.writeVideoAddedToCollection(videoAddedToCollection)
    }

    @StreamListener(VIDEO_REMOVED_FROM_COLLECTION)
    fun videoRemovedFromCollection(videoRemovedFromCollection: VideoRemovedFromCollection) {
        eventWriter.writeVideoRemovedFromCollection(videoRemovedFromCollection)
    }

    @StreamListener(COLLECTION_BOOKMARK_CHANGED)
    fun collectionBookmarkChanged(collectionBookmarkChanged: CollectionBookmarkChanged) {
        eventWriter.writeCollectionBookmarkChanged(collectionBookmarkChanged)
    }

    @StreamListener(COLLECTION_VISIBILITY_CHANGED)
    fun collectionMadePrivate(collectionVisibilityChanged: CollectionVisibilityChanged) {
        eventWriter.writeCollectionVisibilityChanged(collectionVisibilityChanged)
    }

    @StreamListener(COLLECTION_SUBJECTS_CHANGED)
    fun collectionSubjectsChanged(collectionSubjectsChanged: CollectionSubjectsChanged) {
        eventWriter.writeCollectionSubjectsChanged(collectionSubjectsChanged)
    }

    @StreamListener(COLLECTION_AGE_RANGE_CHANGED)
    fun collectionAgeRangeChanged(collectionAgeRangeChanged: CollectionAgeRangeChanged) {
        eventWriter.writeCollectionAgeRangeChanged(collectionAgeRangeChanged)
    }

}
