package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.DatabaseConstants
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createCollectionAgeRangeChanged
import com.boclips.event.service.testsupport.TestFactories.createCollectionBookmarkChanged
import com.boclips.event.service.testsupport.TestFactories.createCollectionSubjectsChanged
import com.boclips.event.service.testsupport.TestFactories.createCollectionVisibilityChanged
import com.boclips.event.service.testsupport.TestFactories.createUserActivated
import com.boclips.event.service.testsupport.TestFactories.createVideoAddedToCollection
import com.boclips.event.service.testsupport.TestFactories.createVideoPlayerInteractedWith
import com.boclips.event.service.testsupport.TestFactories.createVideoRemovedFromCollection
import com.boclips.event.service.testsupport.TestFactories.createVideoSegmentPlayed
import com.boclips.event.service.testsupport.TestFactories.createVideosSearched
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test

class PersistEventIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun userActivated() {
        val event = createUserActivated(userId = "user-id")

        subscriptions.userActivated().send(msg(event))

        assertThat(document().toJson()).contains("user-id")
    }

    @Test
    fun videosSearched() {
        val event = createVideosSearched(query = "hi")

        subscriptions.videosSearched().send(msg(event))

        assertThat(document().toJson()).contains("hi")
    }

    @Test
    fun videoSegmentPlayed() {
        val event = createVideoSegmentPlayed(videoId = "123")

        subscriptions.videoSegmentPlayed().send(msg(event))

        assertThat(document().toJson()).contains("123")
    }

    @Test
    fun videoPlayerInteractedWith() {
        val event = createVideoPlayerInteractedWith(videoId = "123")

        subscriptions.videoPlayerInteractedWith().send(msg(event))

        assertThat(document().toJson()).contains("123")
    }

    @Test
    fun videoAddedToCollection() {
        val event = createVideoAddedToCollection(videoId = "123", collectionId = "456")

        subscriptions.videoAddedToCollection().send(msg(event))

        assertThat(document().toJson()).contains("123")
        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun videoRemovedFromCollection() {
        val event = createVideoRemovedFromCollection(videoId = "123", collectionId = "456")

        subscriptions.videoRemovedFromCollection().send(msg(event))

        assertThat(document().toJson()).contains("123")
        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionBookmarkChanged() {
        val event = createCollectionBookmarkChanged(collectionId = "456", isBookmarked = true)

        subscriptions.collectionBookmarkChanged().send(msg(event))

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("true")
    }

    @Test
    fun collectionVisibilityChanged() {
        val event = createCollectionVisibilityChanged(collectionId = "456", isPublic = false)

        subscriptions.collectionVisibilityChanged().send(msg(event))

        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionSubjectsChanged() {
        val event = createCollectionSubjectsChanged(collectionId = "456", subjects = setOf("Maths"))

        subscriptions.collectionSubjectsChanged().send(msg(event))

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("Maths")
    }

    @Test
    fun collectionAgeRangeChanged() {
        val event = createCollectionAgeRangeChanged(collectionId = "456", rangeMin = 11, rangeMax = 19)

        subscriptions.collectionAgeRangeChanged().send(msg(event))

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("11")
        assertThat(document().toJson()).contains("19")
    }


    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection("events").find().toList().single()
    }
}
