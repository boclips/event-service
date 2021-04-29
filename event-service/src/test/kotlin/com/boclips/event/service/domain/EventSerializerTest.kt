package com.boclips.event.service.domain

import com.boclips.event.infrastructure.EventFields.*
import com.boclips.event.service.testsupport.EventFactory.createCollectionAgeRangeChanged
import com.boclips.event.service.testsupport.EventFactory.createCollectionBookmarkChanged
import com.boclips.event.service.testsupport.EventFactory.createCollectionInteractedWith
import com.boclips.event.service.testsupport.EventFactory.createCollectionSubjectsChanged
import com.boclips.event.service.testsupport.EventFactory.createCollectionVisibilityChanged
import com.boclips.event.service.testsupport.EventFactory.createPageRendered
import com.boclips.event.service.testsupport.EventFactory.createPlatformInteractedWith
import com.boclips.event.service.testsupport.EventFactory.createPlatformInteractedWithAnonymous
import com.boclips.event.service.testsupport.EventFactory.createVideoAddedToCollection
import com.boclips.event.service.testsupport.EventFactory.createVideoInteractedWith
import com.boclips.event.service.testsupport.EventFactory.createVideoPlayerInteractedWith
import com.boclips.event.service.testsupport.EventFactory.createVideoRemovedFromCollection
import com.boclips.event.service.testsupport.OrganisationFactory.createOrganisation
import com.boclips.event.service.testsupport.UserFactory.createUser
import com.boclips.eventbus.domain.ResourceType
import com.boclips.eventbus.domain.page.Viewport
import com.boclips.eventbus.events.base.AbstractEventWithUserId
import com.boclips.eventbus.events.collection.CollectionInteractionType
import com.boclips.eventbus.events.resource.ResourcesSearched
import com.boclips.eventbus.events.searchsuggestions.SearchQueryCompletionsSuggested
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
            .externalUserId("external-user-id")
            .deviceId("device-id")
            .url("https://hello.com/world")
            .build()

        val document = EventSerializer.convertUserEvent(event = event, type = "VIDEO_SEGMENT_PLAYED")

        assertThat(document[TYPE]).isEqualTo("VIDEO_SEGMENT_PLAYED")
        assertThat(document[USER_ID]).isEqualTo("user-id")
        assertThat(document[EXTERNAL_USER_ID]).isEqualTo("external-user-id")
        assertThat(document[DEVICE_ID]).isEqualTo("device-id")
        assertThat(document[URL]).isEqualTo("https://hello.com/world")
        assertThat(document[TIMESTAMP]).isNotNull()
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
            .queryParams(mapOf<String,List<String>>(Pair("age_facets", listOf("01-03","05-07"))))
            .build()

        val document = EventSerializer.convertVideosSearched(videosSearched = event)
        assertThat(document[TYPE]).isEqualTo("VIDEOS_SEARCHED")
        assertThat(document[USER_ID]).isEqualTo("user-1")
        assertThat(document[URL]).isEqualTo("http://example.com/hello")
        assertThat(document[SEARCH_QUERY]).isEqualTo("hello")
        assertThat(document[SEARCH_RESULTS_PAGE_INDEX]).isEqualTo(5)
        assertThat(document[SEARCH_RESULTS_PAGE_SIZE]).isEqualTo(10)
        assertThat(document[SEARCH_RESULTS_TOTAL]).isEqualTo(100L)
        assertThat(document[SEARCH_RESULTS_PAGE_VIDEO_IDS]).asList().containsExactly("v1", "v2")
        assertThat(document[SEARCH_QUERY_PARAMS]).isEqualTo(event.queryParams)
        assertThat((document[TIMESTAMP] as Date).toInstant().atZone(ZoneOffset.UTC)).isEqualTo(
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
            .query("cats")
            .segmentStartSeconds(10)
            .segmentEndSeconds(20)
            .videoIndex(10)
            .videoId("123")
            .deviceId("device-id")
            .build()

        val document = EventSerializer.convertVideoSegmentPlayed(videoSegmentPlayed = event)
        assertThat(document[TYPE]).isEqualTo("VIDEO_SEGMENT_PLAYED")
        assertThat(document[USER_ID]).isEqualTo("user-1")
        assertThat(document[URL]).isEqualTo("http://example.com/video")
        assertThat(document[PLAYBACK_SEGMENT_START_SECONDS]).isEqualTo(10L)
        assertThat(document[PLAYBACK_SEGMENT_END_SECONDS]).isEqualTo(20L)
        assertThat(document[PLAYBACK_VIDEO_INDEX]).isEqualTo(10)
        assertThat(document[VIDEO_ID]).isEqualTo("123")
        assertThat(document[DEVICE_ID]).isEqualTo("device-id")
        assertThat(document[SEARCH_QUERY]).isEqualTo("cats")
        assertThat((document[TIMESTAMP] as Date).toInstant().atZone(ZoneOffset.UTC)).isEqualTo(
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
        val event = createVideoPlayerInteractedWith(
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
        assertThat(document[TYPE]).isEqualTo("VIDEO_PLAYER_INTERACTED_WITH")
        assertThat(document[USER_ID]).isEqualTo("user-1")
        assertThat(document[VIDEO_ID]).isEqualTo("video-id")
        assertThat(document[PLAYER_INTERACTED_WITH_CURRENT_TIME]).isEqualTo(34L)
        assertThat(document[SUBTYPE]).isEqualTo("captions-on")
        assertThat(document[PAYLOAD]).isEqualTo(event.payload)
    }

    @Test
    fun convertVideoAddedToCollection() {
        val event = createVideoAddedToCollection(videoId = "video-id", collectionId = "collection-id")

        val document = EventSerializer.convertVideoAddedToCollection(event)

        assertThat(document[TYPE]).isEqualTo("VIDEO_ADDED_TO_COLLECTION")
        assertThat(document[VIDEO_ID]).isEqualTo("video-id")
        assertThat(document[COLLECTION_ID]).isEqualTo("collection-id")
    }

    @Test
    fun convertVideoRemovedFromCollection() {
        val event = createVideoRemovedFromCollection(videoId = "video-id", collectionId = "collection-id")

        val document = EventSerializer.convertVideoRemovedFromCollection(event)

        assertThat(document[TYPE]).isEqualTo("VIDEO_REMOVED_FROM_COLLECTION")
        assertThat(document[VIDEO_ID]).isEqualTo("video-id")
        assertThat(document[COLLECTION_ID]).isEqualTo("collection-id")
    }

    @Test
    fun `convertCollectionBookmarkChanged bookmarking`() {
        val event = createCollectionBookmarkChanged(collectionId = "collection-id", isBookmarked = true)

        val document = EventSerializer.convertCollectionBookmarkChanged(event)

        assertThat(document[TYPE]).isEqualTo("COLLECTION_BOOKMARK_CHANGED")
        assertThat(document[COLLECTION_ID]).isEqualTo("collection-id")
        assertThat(document[COLLECTION_BOOKMARK_CHANGED_IS_BOOKMARKED]).isEqualTo(true)
    }

    @Test
    fun `convertCollectionBookmarkChanged unbookmarking`() {
        val event = createCollectionBookmarkChanged(collectionId = "collection-id", isBookmarked = false)

        val document = EventSerializer.convertCollectionBookmarkChanged(event)

        assertThat(document[TYPE]).isEqualTo("COLLECTION_BOOKMARK_CHANGED")
        assertThat(document[COLLECTION_ID]).isEqualTo("collection-id")
        assertThat(document[COLLECTION_BOOKMARK_CHANGED_IS_BOOKMARKED]).isEqualTo(false)
    }

    @Test
    fun `convertCollectionVisibilityChanged made public`() {
        val event = createCollectionVisibilityChanged(collectionId = "collection-id", isDiscoverable = true)

        val document = EventSerializer.convertCollectionVisibilityChanged(event)

        assertThat(document[TYPE]).isEqualTo("COLLECTION_VISIBILITY_CHANGED")
        assertThat(document[COLLECTION_ID]).isEqualTo("collection-id")
        assertThat(document[COLLECTION_VISIBILITY_CHANGED_IS_DISCOVERABLE]).isEqualTo(true)
    }

    @Test
    fun `convertCollectionVisibilityChanged made private`() {
        val event = createCollectionVisibilityChanged(collectionId = "collection-id", isDiscoverable = false)

        val document = EventSerializer.convertCollectionVisibilityChanged(event)

        assertThat(document[TYPE]).isEqualTo("COLLECTION_VISIBILITY_CHANGED")
        assertThat(document[COLLECTION_ID]).isEqualTo("collection-id")
        assertThat(document[COLLECTION_VISIBILITY_CHANGED_IS_DISCOVERABLE]).isEqualTo(false)
    }

    @Test
    fun convertCollectionSubjectsChanged() {
        val event =
            createCollectionSubjectsChanged(collectionId = "collection-id", subjects = setOf("Science"))

        val document = EventSerializer.convertCollectionSubjectsChanged(event)

        assertThat(document[TYPE]).isEqualTo("COLLECTION_SUBJECTS_CHANGED")
        assertThat(document[COLLECTION_ID]).isEqualTo("collection-id")
        assertThat(document[COLLECTION_SUBJECTS_CHANGED_SUBJECTS]).asList().containsExactly("Science")
    }

    @Test
    fun convertCollectionAgeRangeChanged() {
        val event =
            createCollectionAgeRangeChanged(collectionId = "collection-1", rangeMin = 5, rangeMax = 19)

        val document = EventSerializer.convertCollectionAgeRangeChanged(event)

        assertThat(document[TYPE]).isEqualTo("COLLECTION_AGE_RANGE_CHANGED")
        assertThat(document[COLLECTION_ID]).isEqualTo("collection-1")
        assertThat(document[COLLECTION_AGE_RANGE_CHANGED_RANGE_MIN]).isEqualTo(5)
        assertThat(document[COLLECTION_AGE_RANGE_CHANGED_RANGE_MAX]).isEqualTo(19)
    }

    @Test
    fun convertCollectionAgeRangeChanged_whenRangeMaxNull() {
        val event =
            createCollectionAgeRangeChanged(collectionId = "collection-1", rangeMin = 5, rangeMax = null)

        val document = EventSerializer.convertCollectionAgeRangeChanged(event)

        assertThat(document[COLLECTION_AGE_RANGE_CHANGED_RANGE_MAX]).isNull()
    }

    @Test
    fun convertCollectionInteractedWith_NavigateToCollectionDetails() {
        val event = createCollectionInteractedWith(
            timestamp = ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneOffset.UTC),
            collectionId = "the-collection-id",
            subtype = CollectionInteractionType.NAVIGATE_TO_COLLECTION_DETAILS,
            user = createUser(id = "user-id"),
            url = "https://boclips.com/collections?q=hello"
        )

        val document = EventSerializer.convertCollectionInteractedWith(event)

        assertThat(document[TYPE]).isEqualTo("COLLECTION_INTERACTED_WITH")
        assertThat(document[TIMESTAMP]).isEqualTo(
            Date.from(
                ZonedDateTime.parse("2019-05-12T12:14:15.100Z").toInstant()
            )
        )
        assertThat(document[COLLECTION_ID]).isEqualTo("the-collection-id")
        assertThat(document[SUBTYPE]).isEqualTo("NAVIGATE_TO_COLLECTION_DETAILS")
        assertThat(document[USER_ID]).isEqualTo("user-id")
        assertThat(document[URL]).isEqualTo("https://boclips.com/collections?q=hello")
    }

    @Test
    fun convertCollectionInteractedWith_VisitLessonGuide() {
        val event = createCollectionInteractedWith(
            timestamp = ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneOffset.UTC),
            collectionId = "the-collection-id",
            subtype = CollectionInteractionType.VISIT_LESSON_GUIDE,
            user = createUser(id = "user-id"),
            url = "https://boclips.com/collections?q=hello"
        )

        val document = EventSerializer.convertCollectionInteractedWith(event)

        assertThat(document[TYPE]).isEqualTo("COLLECTION_INTERACTED_WITH")
        assertThat(document[TIMESTAMP]).isEqualTo(
            Date.from(
                ZonedDateTime.parse("2019-05-12T12:14:15.100Z").toInstant()
            )
        )
        assertThat(document[COLLECTION_ID]).isEqualTo("the-collection-id")
        assertThat(document[SUBTYPE]).isEqualTo("VISIT_LESSON_GUIDE")
        assertThat(document[USER_ID]).isEqualTo("user-id")
        assertThat(document[URL]).isEqualTo("https://boclips.com/collections?q=hello")
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

        assertThat(document[TYPE]).isEqualTo("VIDEO_INTERACTED_WITH")
        assertThat(document[TIMESTAMP]).isEqualTo(Date.from(ZonedDateTime.parse("2019-05-12T12:14:15Z").toInstant()))
        assertThat(document[VIDEO_ID]).isEqualTo("the-video-id")
        assertThat(document[SUBTYPE]).isEqualTo("copy-share-link")
        assertThat(document[PAYLOAD]).isEqualTo(mapOf("additional-field" to "bunny"))
        assertThat(document[USER_ID]).isEqualTo("user-id")
        assertThat(document[URL]).isEqualTo("https://boclips.com/videos?q=hello")
    }

    @Test
    fun convertPageRendered() {
        val event = createPageRendered(
            userId = "my-test-id",
            url = "http://teachers.boclips.com/test/page?data=123",
            viewport = Viewport(320, 640),
            timestamp = ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneOffset.UTC)
        )

        val document = EventSerializer.convertPageRendered(event)

        assertThat(document[USER_ID]).isEqualTo("my-test-id")
        assertThat(document[URL]).isEqualTo("http://teachers.boclips.com/test/page?data=123")
        assertThat(document[VIEWPORT_WIDTH]).isEqualTo(320)
        assertThat(document[VIEWPORT_HEIGHT]).isEqualTo(640)
        assertThat(document[URL]).isEqualTo("http://teachers.boclips.com/test/page?data=123")
        assertThat(document[TIMESTAMP]).isEqualTo(
            Date.from(
                ZonedDateTime.parse("2019-05-12T12:14:15.1Z").toInstant()
            )
        )
        assertThat(document[TYPE]).isEqualTo("PAGE_RENDERED")
    }

    @Test
    fun ignoresNullViewportWhenConvertingPageRenderedEvent() {
        val event = createPageRendered(
            userId = "my-test-id",
            url = "http://teachers.boclips.com/test/page?data=123",
            viewport = null,
            timestamp = ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneOffset.UTC)
        )

        val document = EventSerializer.convertPageRendered(event)

        assertThat(document[USER_ID]).isEqualTo("my-test-id")
        assertThat(document[URL]).isEqualTo("http://teachers.boclips.com/test/page?data=123")
        assertThat(document[VIEWPORT_WIDTH]).isNull()
        assertThat(document[VIEWPORT_HEIGHT]).isNull()
        assertThat(document[URL]).isEqualTo("http://teachers.boclips.com/test/page?data=123")
        assertThat(document[TIMESTAMP]).isEqualTo(
            Date.from(
                ZonedDateTime.parse("2019-05-12T12:14:15.1Z").toInstant()
            )
        )
        assertThat(document[TYPE]).isEqualTo("PAGE_RENDERED")
    }

    @Test
    fun convertPlatformInteractedWith() {
        val event = createPlatformInteractedWith(
            subtype = "HELP_CLICKED",
            userId = "my-test-id",
            url = "http://teachers.boclips.com/test/page?data=123",
            timestamp = ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneOffset.UTC)
        )

        val document = EventSerializer.convertPlatformInteractedWith(event)

        assertThat(document[TYPE]).isEqualTo("PLATFORM_INTERACTED_WITH")
        assertThat(document[SUBTYPE]).isEqualTo("HELP_CLICKED")
        assertThat(document[USER_ID]).isEqualTo("my-test-id")
        assertThat(document[URL]).isEqualTo("http://teachers.boclips.com/test/page?data=123")
        assertThat(document[TIMESTAMP]).isEqualTo(
            Date.from(
                ZonedDateTime.parse("2019-05-12T12:14:15.1Z").toInstant()
            )
        )
    }

    @Test
    fun convertAnonymousPlatformInteractedWith() {
        val event = createPlatformInteractedWithAnonymous(
            subtype = "HELP_CLICKED",
            url = "http://teachers.boclips.com/test/page?data=123",
            timestamp = ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneOffset.UTC)
        )

        val document = EventSerializer.convertPlatformInteractedWith(event)

        assertThat(document[TYPE]).isEqualTo("PLATFORM_INTERACTED_WITH")
        assertThat(document[SUBTYPE]).isEqualTo("HELP_CLICKED")
        assertThat(document[URL]).isEqualTo("http://teachers.boclips.com/test/page?data=123")
        assertThat(document[TIMESTAMP]).isEqualTo(
            Date.from(
                ZonedDateTime.parse("2019-05-12T12:14:15.1Z").toInstant()
            )
        )
    }

    @Test
    fun `convertUserExpired with no organisation`() {
        val event = UserExpired.builder()
            .user(createUser(id = "my-test-id"))
            .timestamp(ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100, ZoneOffset.UTC))
            .build()

        val document = EventSerializer.convertUserExpired(event)

        assertThat(document[TYPE]).isEqualTo("USER_EXPIRED")
        assertThat(document[USER_ID]).isEqualTo("my-test-id")
        assertThat(document[TIMESTAMP]).isEqualTo(Date.from(ZonedDateTime.parse("2019-05-12T12:14:15Z").toInstant()))
    }

    @Test
    fun `convertUserExpired with an organisation, no grandparent organisation`() {
        val event = UserExpired.builder()
            .user(createUser(id = "my-test-id", organisation = createOrganisation(id = "org-id", type = "SCHOOL")))
            .timestamp(ZonedDateTime.of(2019, 5, 12, 12, 14, 15, 100000000, ZoneId.of("UTC")))
            .build()

        val document = EventSerializer.convertUserExpired(event)

        assertThat(document[TYPE]).isEqualTo("USER_EXPIRED")
        assertThat(document[USER_ID]).isEqualTo("my-test-id")
        assertThat(document[ORGANISATION_ID]).isEqualTo("org-id")
        assertThat(document[ORGANISATION_TYPE]).isEqualTo("SCHOOL")
        assertThat(document[TIMESTAMP]).isEqualTo(
            Date.from(
                ZonedDateTime.parse("2019-05-12T12:14:15.100Z").toInstant()
            )
        )
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

        assertThat(document[TYPE]).isEqualTo("USER_EXPIRED")
        assertThat(document[USER_ID]).isEqualTo("my-test-id")
        assertThat(document[ORGANISATION_ID]).isEqualTo("org-id")
        assertThat(document[ORGANISATION_TYPE]).isEqualTo("SCHOOL")
        assertThat(document[ORGANISATION_PARENT_ID]).isEqualTo("grandparent-id")
        assertThat(document[ORGANISATION_PARENT_TYPE]).isEqualTo("DISTRICT")
        assertThat(document[TIMESTAMP]).isEqualTo(Date.from(ZonedDateTime.parse("2019-05-12T12:14:15Z").toInstant()))
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
            .pageResourceIds(listOf("id-1", "id-2"))
            .timestamp(ZonedDateTime.of(2023, 5, 12, 12, 14, 15, 100, ZoneOffset.UTC))
            .queryParams(mapOf<String,List<String>>(Pair("age_facets", listOf("01-03","05-07"))))
            .build()

        val document = EventSerializer.convertResourcesSearched(event)
        assertThat(document[TYPE]).isEqualTo("RESOURCES_SEARCHED")
        assertThat(document[USER_ID]).isEqualTo("roman-user")
        assertThat(document[URL]).isEqualTo("www.marcus.it")
        assertThat(document[SEARCH_QUERY]).isEqualTo("nature")
        assertThat(document[SEARCH_RESOURCE_TYPE]).isEqualTo(ResourceType.COLLECTION)
        assertThat(document[SEARCH_RESULTS_PAGE_INDEX]).isEqualTo(2)
        assertThat(document[SEARCH_RESULTS_PAGE_SIZE]).isEqualTo(7)
        assertThat(document[SEARCH_RESULTS_TOTAL]).isEqualTo(23L)
        assertThat(document[SEARCH_RESULTS_PAGE_RESOURCE_IDS]).asList().containsExactly("id-1", "id-2")
        assertThat(document[SEARCH_QUERY_PARAMS]).isEqualTo(event.queryParams)
        assertThat(document[TIMESTAMP]).isEqualTo(Date.from(ZonedDateTime.parse("2023-05-12T12:14:15Z").toInstant()))
    }

    @Test
    fun `convert SearchQueryCompletionsSuggested`() {
        val event = SearchQueryCompletionsSuggested.builder()
                .userId("user-id")
                .url("www.example.com")
                .searchQuery("bio")
                .completionId("completion-id")
                .impressions(listOf("biology", "biodiversity"))
                .componentId("component-id")
                .timestamp(ZonedDateTime.of(2023, 5, 12, 12, 14, 15, 100, ZoneOffset.UTC)).build()

        val document = EventSerializer.convertSearchQueryCompletionsSuggested(event)

        assertThat(document[TYPE]).isEqualTo("SEARCH_QUERY_COMPLETIONS_SUGGESTED")
        assertThat(document[USER_ID]).isEqualTo("user-id")
        assertThat(document[URL]).isEqualTo("www.example.com")
        assertThat(document[SEARCH_QUERY]).isEqualTo("bio")
        assertThat(document[COMPLETION_ID]).isEqualTo("completion-id")
        assertThat(document[IMPRESSIONS]).asList().containsExactly("biology", "biodiversity")
        assertThat(document[COMPONENT_ID]).isEqualTo("component-id")

        assertThat(document[TIMESTAMP]).isEqualTo(Date.from(ZonedDateTime.parse("2023-05-12T12:14:15Z").toInstant()))
    }
}


