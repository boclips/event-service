package com.boclips.event.service.domain

import com.boclips.event.service.testsupport.TestFactories
import com.boclips.event.service.testsupport.TestFactories.createCollectionInteractedWith
import com.boclips.event.service.testsupport.TestFactories.createOrganisation
import com.boclips.event.service.testsupport.TestFactories.createPageRendered
import com.boclips.event.service.testsupport.TestFactories.createUser
import com.boclips.event.service.testsupport.TestFactories.createVideoInteractedWith
import com.boclips.eventbus.domain.ResourceType
import com.boclips.eventbus.events.base.AbstractEventWithUserId
import com.boclips.eventbus.events.collection.CollectionInteractionType
import com.boclips.eventbus.events.resource.ResourcesSearched
import com.boclips.eventbus.events.user.UserExpired
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideosSearched
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date

class EventSerializerTest {

    @Test
    fun `converts EventWithUserId`() {
        val event = AbstractEventWithUserId.builder()
                .userId("user-id")
                .overrideUserId("override-user-id")
                .url("https://hello.com/world")
                .build()

        val document = EventSerializer.convertUserEvent(event = event, type = "VIDEO_SEGMENT_PLAYED")

        assertThat(document["type"]).isEqualTo("VIDEO_SEGMENT_PLAYED")
        assertThat(document["userId"]).isEqualTo("user-id")
        assertThat(document["overrideUserId"]).isEqualTo("override-user-id")
        assertThat(document["url"]).isEqualTo("https://hello.com/world")
        assertThat(document["timestamp"]).isNotNull()
    }

    @Test
    fun videosSearched() {
        val event = VideosSearched.builder()
            .timestamp(ZonedDateTime.parse("2018-05-31T13:45:59Z"))
            .userId("user-1")
            .url("http://example.com/hello")
            .pageIndex(5)
            .pageSize(10)
            .query("hello")
            .pageVideoIds(listOf("v1", "v2"))
            .totalResults(100)
            .build()

        val document = EventSerializer.convertVideosSearched(videosSearched = event)
        assertThat(document["type"]).isEqualTo("VIDEOS_SEARCHED")
        assertThat(document["userId"]).isEqualTo("user-1")
        assertThat(document["url"]).isEqualTo("http://example.com/hello")
        assertThat(document["query"]).isEqualTo("hello")
        assertThat(document["pageIndex"]).isEqualTo(5)
        assertThat(document["pageSize"]).isEqualTo(10)
        assertThat(document["totalResults"]).isEqualTo(100L)
        assertThat(document["pageVideoIds"]).asList().containsExactly("v1", "v2")
        assertThat((document["timestamp"] as Date).toInstant().atZone(ZoneOffset.UTC)).isEqualTo(
            ZonedDateTime.of(
                2018,
                5,
                31,
                13,
                45,
                59,
                0,
                ZoneOffset.UTC
            )
        )
    }

    @Test
    fun videoSegmentPlayed() {
        val event = VideoSegmentPlayed.builder()
            .timestamp(ZonedDateTime.parse("2019-05-31T13:45:59Z"))
            .userId("user-1")
            .url("http://example.com/video")
            .segmentStartSeconds(10)
            .segmentEndSeconds(20)
            .videoIndex(10)
            .videoId("123")
            .playbackDevice("device-id")
            .build()

        val document = EventSerializer.convertVideoSegmentPlayed(videoSegmentPlayed = event)
        assertThat(document["type"]).isEqualTo("VIDEO_SEGMENT_PLAYED")
        assertThat(document["userId"]).isEqualTo("user-1")
        assertThat(document["url"]).isEqualTo("http://example.com/video")
        assertThat(document["segmentStartSeconds"]).isEqualTo(10L)
        assertThat(document["segmentEndSeconds"]).isEqualTo(20L)
        assertThat(document["videoIndex"]).isEqualTo(10)
        assertThat(document["videoId"]).isEqualTo("123")
        assertThat(document["playbackDevice"]).isEqualTo("device-id")
        assertThat((document["timestamp"] as Date).toInstant().atZone(ZoneOffset.UTC)).isEqualTo(
            ZonedDateTime.of(
                2019,
                5,
                31,
                13,
                45,
                59,
                0,
                ZoneOffset.UTC
            )
        )
    }

    @Test
    fun videoPlayerInteractedWith() {
        val event = TestFactories.createVideoPlayerInteractedWith(
            videoId = "video-id",
            currentTime = 34,
            subtype = "captions-on",
            payload = mapOf<String, Any>(
                Pair("kind", "caption-kind"),
                Pair("language", "caption-language"),
                Pair("id", "caption-id"),
                Pair("label", "caption-label")
            )
        )

        val document = EventSerializer.convertVideoPlayerInteractedWith(event)
        assertThat(document["type"]).isEqualTo("VIDEO_PLAYER_INTERACTED_WITH")
        assertThat(document["userId"]).isEqualTo("user-1")
        assertThat(document["videoId"]).isEqualTo("video-id")
        assertThat(document["currentTime"]).isEqualTo(34L)
        assertThat(document["subtype"]).isEqualTo("captions-on")
        assertThat(document["payload"]).isEqualTo(event.payload)
    }

    @Test
    fun convertVideoAddedToCollection() {
        val event = TestFactories.createVideoAddedToCollection(videoId = "video-id", collectionId = "collection-id")

        val document = EventSerializer.convertVideoAddedToCollection(event)

        assertThat(document["type"]).isEqualTo("VIDEO_ADDED_TO_COLLECTION")
        assertThat(document["videoId"]).isEqualTo("video-id")
        assertThat(document["collectionId"]).isEqualTo("collection-id")
    }

    @Test
    fun convertVideoRemovedFromCollection() {
        val event = TestFactories.createVideoRemovedFromCollection(videoId = "video-id", collectionId = "collection-id")

        val document = EventSerializer.convertVideoRemovedFromCollection(event)

        assertThat(document["type"]).isEqualTo("VIDEO_REMOVED_FROM_COLLECTION")
        assertThat(document["videoId"]).isEqualTo("video-id")
        assertThat(document["collectionId"]).isEqualTo("collection-id")
    }

    @Test
    fun `convertCollectionBookmarkChanged bookmarking`() {
        val event = TestFactories.createCollectionBookmarkChanged(collectionId = "collection-id", isBookmarked = true)

        val document = EventSerializer.convertCollectionBookmarkChanged(event)

        assertThat(document["type"]).isEqualTo("COLLECTION_BOOKMARK_CHANGED")
        assertThat(document["collectionId"]).isEqualTo("collection-id")
        assertThat(document["isBookmarked"]).isEqualTo(true)
    }

    @Test
    fun `convertCollectionBookmarkChanged unbookmarking`() {
        val event = TestFactories.createCollectionBookmarkChanged(collectionId = "collection-id", isBookmarked = false)

        val document = EventSerializer.convertCollectionBookmarkChanged(event)

        assertThat(document["type"]).isEqualTo("COLLECTION_BOOKMARK_CHANGED")
        assertThat(document["collectionId"]).isEqualTo("collection-id")
        assertThat(document["isBookmarked"]).isEqualTo(false)
    }

    @Test
    fun `convertCollectionVisibilityChanged made public`() {
        val event = TestFactories.createCollectionVisibilityChanged(collectionId = "collection-id", isPublic = true)

        val document = EventSerializer.convertCollectionVisibilityChanged(event)

        assertThat(document["type"]).isEqualTo("COLLECTION_VISIBILITY_CHANGED")
        assertThat(document["collectionId"]).isEqualTo("collection-id")
        assertThat(document["isPublic"]).isEqualTo(true)
    }

    @Test
    fun `convertCollectionVisibilityChanged made private`() {
        val event = TestFactories.createCollectionVisibilityChanged(collectionId = "collection-id", isPublic = false)

        val document = EventSerializer.convertCollectionVisibilityChanged(event)

        assertThat(document["type"]).isEqualTo("COLLECTION_VISIBILITY_CHANGED")
        assertThat(document["collectionId"]).isEqualTo("collection-id")
        assertThat(document["isPublic"]).isEqualTo(false)
    }

    @Test
    fun convertCollectionSubjectsChanged() {
        val event =
            TestFactories.createCollectionSubjectsChanged(collectionId = "collection-id", subjects = setOf("Science"))

        val document = EventSerializer.convertCollectionSubjectsChanged(event)

        assertThat(document["type"]).isEqualTo("COLLECTION_SUBJECTS_CHANGED")
        assertThat(document["collectionId"]).isEqualTo("collection-id")
        assertThat(document["subjects"]).asList().containsExactly("Science")
    }

    @Test
    fun convertCollectionAgeRangeChanged() {
        val event =
            TestFactories.createCollectionAgeRangeChanged(collectionId = "collection-1", rangeMin = 5, rangeMax = 19)

        val document = EventSerializer.convertCollectionAgeRangeChanged(event)

        assertThat(document["type"]).isEqualTo("COLLECTION_AGE_RANGE_CHANGED")
        assertThat(document["collectionId"]).isEqualTo("collection-1")
        assertThat(document["rangeMin"]).isEqualTo(5)
        assertThat(document["rangeMax"]).isEqualTo(19)
    }

    @Test
    fun convertCollectionAgeRangeChanged_whenRangeMaxNull() {
        val event =
            TestFactories.createCollectionAgeRangeChanged(collectionId = "collection-1", rangeMin = 5, rangeMax = null)

        val document = EventSerializer.convertCollectionAgeRangeChanged(event)

        assertThat(document["rangeMax"]).isNull()
    }

    @Test
    fun convertCollectionInteractedWith() {
        val event = createCollectionInteractedWith(
            timestamp = ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneOffset.UTC),
            collectionId = "the-collection-id",
            subtype = CollectionInteractionType.NAVIGATE_TO_COLLECTION_DETAILS,
            user = createUser(id = "user-id"),
            url = "https://boclips.com/collections?q=hello"
        )

        val document = EventSerializer.convertCollectionInteractedWith(event)

        assertThat(document["type"]).isEqualTo("COLLECTION_INTERACTED_WITH")
        assertThat(document["timestamp"]).isEqualTo(Date.from(ZonedDateTime.parse("2019-05-12T12:14:15.100Z").toInstant()))
        assertThat(document["collectionId"]).isEqualTo("the-collection-id")
        assertThat(document["subtype"]).isEqualTo("NAVIGATE_TO_COLLECTION_DETAILS")
        assertThat(document["userId"]).isEqualTo("user-id")
        assertThat(document["url"]).isEqualTo("https://boclips.com/collections?q=hello")
    }

    @Test
    fun convertVideoInteractedWith() {
        val event = createVideoInteractedWith(
            timestamp = ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100, ZoneOffset.UTC),
            videoId = "the-video-id",
            subtype = "copy-share-link",
            payload = HashMap<String, Any>().apply { put("additional-field", "bunny") },
            user = createUser(id = "user-id"),
            url = "https://boclips.com/videos?q=hello"
        )

        val document = EventSerializer.convertVideoInteractedWith(event)

        assertThat(document["type"]).isEqualTo("VIDEO_INTERACTED_WITH")
        assertThat(document["timestamp"]).isEqualTo(Date.from(ZonedDateTime.parse("2019-05-12T12:14:15Z").toInstant()))
        assertThat(document["videoId"]).isEqualTo("the-video-id")
        assertThat(document["subtype"]).isEqualTo("copy-share-link")
        assertThat(document["payload"]).isEqualTo(mapOf("additional-field" to "bunny"))
        assertThat(document["userId"]).isEqualTo("user-id")
        assertThat(document["url"]).isEqualTo("https://boclips.com/videos?q=hello")
    }

    @Test
    fun convertPageRendered() {
        val event = createPageRendered(
            userId = "my-test-id",
            url = "http://teachers.boclips.com/test/page?data=123",
            timestamp = ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneOffset.UTC)
        )

        val document = EventSerializer.convertPageRendered(event)

        assertThat(document["userId"]).isEqualTo("my-test-id")
        assertThat(document["url"]).isEqualTo("http://teachers.boclips.com/test/page?data=123")
        assertThat(document["timestamp"]).isEqualTo(Date.from(ZonedDateTime.parse("2019-05-12T12:14:15.1Z").toInstant()))
        assertThat(document["type"]).isEqualTo("PAGE_RENDERED")
    }

    @Test
    fun `convertUserExpired with no organisation`() {
        val event = UserExpired.builder()
            .user(createUser(id = "my-test-id"))
            .timestamp(ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100, ZoneOffset.UTC))
            .build()

        val document = EventSerializer.convertUserExpired(event)

        assertThat(document["type"]).isEqualTo("USER_EXPIRED")
        assertThat(document["userId"]).isEqualTo("my-test-id")
        assertThat(document["timestamp"]).isEqualTo(Date.from(ZonedDateTime.parse("2019-05-12T12:14:15Z").toInstant()))
    }

    @Test
    fun `convertUserExpired with an organisation, no grandparent organisation`() {
        val event = UserExpired.builder()
            .user(createUser(id = "my-test-id", organisation = createOrganisation(id = "org-id", type = "SCHOOL")))
            .timestamp(ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneId.of("UTC")))
            .build()

        val document = EventSerializer.convertUserExpired(event)

        assertThat(document["type"]).isEqualTo("USER_EXPIRED")
        assertThat(document["userId"]).isEqualTo("my-test-id")
        assertThat(document["organisationId"]).isEqualTo("org-id")
        assertThat(document["organisationType"]).isEqualTo("SCHOOL")
        assertThat(document["userId"]).isEqualTo("my-test-id")
        assertThat(document["timestamp"]).isEqualTo(Date.from(ZonedDateTime.parse("2019-05-12T12:14:15.100Z").toInstant()))
    }

    @Test
    fun `convertUserExpired with an organisation, with grandparent organisation`() {
        val event = UserExpired.builder()
            .user(
                createUser(
                    id = "my-test-id",
                    organisation = createOrganisation(
                        id = "org-id",
                        type = "SCHOOL",
                        parent = createOrganisation(id = "grandparent-id", type = "DISTRICT")
                    )
                )
            )
            .timestamp(ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100, ZoneOffset.UTC))
            .build()

        val document = EventSerializer.convertUserExpired(event)

        assertThat(document["type"]).isEqualTo("USER_EXPIRED")
        assertThat(document["userId"]).isEqualTo("my-test-id")
        assertThat(document["organisationId"]).isEqualTo("org-id")
        assertThat(document["organisationType"]).isEqualTo("SCHOOL")
        assertThat(document["organisationParentId"]).isEqualTo("grandparent-id")
        assertThat(document["organisationParentType"]).isEqualTo("DISTRICT")
        assertThat(document["userId"]).isEqualTo("my-test-id")
        assertThat(document["timestamp"]).isEqualTo(Date.from(ZonedDateTime.parse("2019-05-12T12:14:15Z").toInstant()))
    }

    @Test
    fun `convert resourcesSearched`() {
        val event = ResourcesSearched.builder()
            .userId("roman-user")
            .url("www.marcus.it")
            .query("nature")
            .resourceType(ResourceType.COLLECTION)
            .pageIndex(2)
            .pageSize(7)
            .totalResults(23)
            .pageResourceIds(listOf("id-1","id-2"))
            .timestamp(ZonedDateTime.of(2023, 5, 12, 12, 14, 15, 100, ZoneOffset.UTC)).build()

        val document = EventSerializer.convertResourcesSearched(event)
        assertThat(document["type"]).isEqualTo("RESOURCES_SEARCHED")
        assertThat(document["userId"]).isEqualTo("roman-user")
        assertThat(document["url"]).isEqualTo("www.marcus.it")
        assertThat(document["query"]).isEqualTo("nature")
        assertThat(document["resourceType"]).isEqualTo(ResourceType.COLLECTION)
        assertThat(document["pageIndex"]).isEqualTo(2)
        assertThat(document["pageSize"]).isEqualTo(7)
        assertThat(document["totalResults"]).isEqualTo(23L)
        assertThat(document["pageResourceIds"]).asList().containsExactly("id-1", "id-2")
        assertThat(document["timestamp"]).isEqualTo(Date.from(ZonedDateTime.parse("2023-05-12T12:14:15Z").toInstant()))
    }
}


