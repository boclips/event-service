package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.mongodb.DatabaseConstants
import com.boclips.event.service.infrastructure.mongodb.MongoEventWriter
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.EventFactory.createCollectionAgeRangeChanged
import com.boclips.event.service.testsupport.EventFactory.createCollectionBookmarkChanged
import com.boclips.event.service.testsupport.EventFactory.createCollectionInteractedWith
import com.boclips.event.service.testsupport.EventFactory.createCollectionSubjectsChanged
import com.boclips.event.service.testsupport.EventFactory.createCollectionVisibilityChanged
import com.boclips.event.service.testsupport.EventFactory.createPageRendered
import com.boclips.event.service.testsupport.EventFactory.createPlatformInteractedWith
import com.boclips.event.service.testsupport.EventFactory.createPlatformInteractedWithAnonymous
import com.boclips.event.service.testsupport.EventFactory.createResourcesSearched
import com.boclips.event.service.testsupport.EventFactory.createSearchQueryCompletionsSuggested
import com.boclips.event.service.testsupport.EventFactory.createVideoAddedToCollection
import com.boclips.event.service.testsupport.EventFactory.createVideoInteractedWith
import com.boclips.event.service.testsupport.EventFactory.createVideoPlayerInteractedWith
import com.boclips.event.service.testsupport.EventFactory.createVideoRemovedFromCollection
import com.boclips.event.service.testsupport.EventFactory.createVideoSegmentPlayed
import com.boclips.event.service.testsupport.EventFactory.createVideosSearched
import com.boclips.event.service.testsupport.UserFactory.createUser
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test

class PersistEventIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun videosSearched() {
        val event = createVideosSearched(query = "hi")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("hi")
    }

    @Test
    fun videoSegmentPlayed() {
        val event = createVideoSegmentPlayed(videoId = "123", query = "cats")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("123")
        assertThat(document().toJson()).contains("cats")
    }

    @Test
    fun videoPlayerInteractedWith() {
        val event = createVideoPlayerInteractedWith(videoId = "123")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("123")
    }

    @Test
    fun videoAddedToCollection() {
        val event = createVideoAddedToCollection(videoId = "123", collectionId = "456")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("123")
        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun videoRemovedFromCollection() {
        val event = createVideoRemovedFromCollection(videoId = "123", collectionId = "456")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("123")
        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionBookmarkChanged() {
        val event = createCollectionBookmarkChanged(collectionId = "456", isBookmarked = true, user = createUser())

        eventBus.publish(event)

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("true")
    }

    @Test
    fun collectionVisibilityChanged() {
        val event = createCollectionVisibilityChanged(collectionId = "456", isDiscoverable = false)

        eventBus.publish(event)

        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionSubjectsChanged() {
        val event = createCollectionSubjectsChanged(collectionId = "456", subjects = setOf("Maths"))

        eventBus.publish(event)

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("Maths")
    }

    @Test
    fun collectionAgeRangeChanged() {
        val event = createCollectionAgeRangeChanged(collectionId = "456", rangeMin = 11, rangeMax = 19)

        eventBus.publish(event)

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("11")
        assertThat(document().toJson()).contains("19")
    }

    @Test
    fun collectionInteractedWith() {
        val event = createCollectionInteractedWith(collectionId = "test-id")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("test-id")
    }

    @Test
    fun videoInteractedWith() {
        val event = createVideoInteractedWith(videoId = "the video id")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("the video id")
    }

    @Test
    fun pageRendered() {
        val event = createPageRendered(url = "http://teachers.boclips.com/my-videos")
        eventBus.publish(event)
        assertThat(document().toJson()).contains("http://teachers.boclips.com/my-videos")
    }

    @Test
    fun resourcesSearched() {
        val event = createResourcesSearched(query = "roman empire sharks")
        eventBus.publish(event)
        assertThat(document().toJson()).contains("roman empire sharks")
    }

    @Test
    fun platformInteractedWith() {
        val event = createPlatformInteractedWith(subtype = "REMOTE_LEARNING_BANNER_CLICKED")
        eventBus.publish(event)
        assertThat(document().toJson()).contains("PLATFORM_INTERACTED_WITH")
        assertThat(document().toJson()).contains("REMOTE_LEARNING_BANNER_CLICKED")
    }

    @Test
    fun platformInteractedWithAnonymous() {
        val event = createPlatformInteractedWithAnonymous(subtype = "ONBOARDING_PAGE_2_STARTED")
        eventBus.publish(event)
        assertThat(document().toJson()).contains("PLATFORM_INTERACTED_WITH")
        assertThat(document().toJson()).contains("ONBOARDING_PAGE_2_STARTED")
    }

    @Test
    fun searchQueryCompletionsSuggested() {
        val event = createSearchQueryCompletionsSuggested(searchQuery = "maths")
        eventBus.publish(event)
        assertThat(document().toJson()).contains("maths")
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(MongoEventWriter.COLLECTION_NAME).find()
            .toList().single()
    }
}
