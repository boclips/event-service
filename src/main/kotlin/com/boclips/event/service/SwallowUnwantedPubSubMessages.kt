package com.boclips.event.service

import com.boclips.events.config.Subscriptions
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Component

@Component
class SwallowUnwantedPubSubMessages {

    @StreamListener(Subscriptions.VIDEO_PLAYBACK_SYNC_REQUESTED)
    fun videoPlaybackSyncRequested() {}

    @StreamListener(Subscriptions.VIDEO_ANALYSIS_REQUESTED)
    fun videoAnalysisRequested() {}

    @StreamListener(Subscriptions.VIDEO_INDEXED)
    fun videoIndexed() {}

    @StreamListener(Subscriptions.VIDEO_ANALYSED)
    fun videoAnalysed() {}

    @StreamListener(Subscriptions.LEGACY_ORDER_SUBMITTED)
    fun legacyOrderSubmitted() {}

    @StreamListener(Subscriptions.COLLECTION_RENAMED)
    fun collectionRenamed() {}

    @StreamListener(Subscriptions.VIDEOS_INCLUSION_IN_SEARCH_REQUESTED)
    fun videosInclusionInSearchRequested() {}

    @StreamListener(Subscriptions.VIDEOS_EXCLUSION_FROM_SEARCH_REQUESTED)
    fun videosExclusionFromSearchRequested() {}

    @StreamListener(Subscriptions.VIDEO_SUBJECT_CLASSIFICATION_REQUESTED)
    fun videoSubjectClassificationRequested() {}

    @StreamListener(Subscriptions.VIDEO_SUBJECT_CLASSIFIED)
    fun videoSubjectClassified() {}

    @StreamListener(Subscriptions.VIDEO_CAPTIONS_CREATED)
    fun videoCaptionsCreated() {}

    @StreamListener(Subscriptions.VIDEO_TRANSCRIPT_CREATED)
    fun videoTranscriptCreated() {}
}
