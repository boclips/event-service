package com.boclips.event.service.infrastructure

import com.boclips.event.service.testsupport.TestFactories
import com.boclips.eventbus.domain.user.User
import com.boclips.eventbus.events.user.UserActivated
import com.boclips.eventbus.events.video.VideoSegmentPlayed
import com.boclips.eventbus.events.video.VideosSearched
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class EventToDocumentConverterTest {

    @Test
    fun `userIsBoclips marked true for boclips email addresses`() {
        val event = TestFactories.createUserActivated(isBoclipsEmployee = true)

        val document = EventToDocumentConverter.convertUserActivated(event)

        assertThat(document.getBoolean("userIsBoclips")).isTrue()
    }

    @Test
    fun userActivated() {
        val event = UserActivated.builder()
                .timestamp(Date.from(ZonedDateTime.parse("2018-05-31T13:45:59Z").toInstant()))
                .user(User.builder()
                        .id("user-1")
                        .isBoclipsEmployee(false)
                        .build()
                )
                .totalUsers(100)
                .activatedUsers(50)
                .build()

        val document = EventToDocumentConverter.convertUserActivated(event)

        assertThat(document.getString("type")).isEqualTo("USER_ACTIVATED")
        assertThat(document.getString("userId")).isEqualTo("user-1")
        assertThat(document.getBoolean("userIsBoclips")).isFalse()
        assertThat(document.getDate("timestamp").toInstant().atZone(ZoneOffset.UTC)).isEqualTo(ZonedDateTime.of(2018, 5, 31, 13, 45, 59, 0, ZoneOffset.UTC))
        assertThat(document.getLong("totalUsers")).isEqualTo(100)
        assertThat(document.getLong("activatedUsers")).isEqualTo(50)
    }

    @Test
    fun videosSearched() {
        val event = VideosSearched.builder()
                .timestamp(Date.from(ZonedDateTime.parse("2018-05-31T13:45:59Z").toInstant()))
                .user(User.builder()
                        .id("user-1")
                        .isBoclipsEmployee(false)
                        .build()
                )
                .url("http://example.com/hello")
                .pageIndex(5)
                .pageSize(10)
                .query("hello")
                .pageVideoIds(listOf("v1", "v2"))
                .totalResults(100)
                .build()

        val document = EventToDocumentConverter.convertVideosSearched(videosSearched = event)
        assertThat(document.getString("type")).isEqualTo("VIDEOS_SEARCHED")
        assertThat(document.getString("userId")).isEqualTo("user-1")
        assertThat(document.getBoolean("userIsBoclips")).isEqualTo(false)
        assertThat(document.getDate("timestamp").toInstant().atZone(ZoneOffset.UTC)).isEqualTo(ZonedDateTime.of(2018, 5, 31, 13, 45, 59, 0, ZoneOffset.UTC))
        assertThat(document.getString("url")).isEqualTo("http://example.com/hello")
        assertThat(document.getString("query")).isEqualTo("hello")
        assertThat(document.getInteger("pageIndex")).isEqualTo(5)
        assertThat(document.getInteger("pageSize")).isEqualTo(10)
        assertThat(document.getLong("totalResults")).isEqualTo(100)
        assertThat(document.getList("pageVideoIds", String::class.java)).containsExactly("v1", "v2")
    }

    @Test
    fun videoSegmentPlayed() {
        val event = VideoSegmentPlayed.builder()
                .timestamp(Date.from(ZonedDateTime.parse("2019-05-31T13:45:59Z").toInstant()))
                .user(User.builder()
                        .id("user-1")
                        .isBoclipsEmployee(true)
                        .build()
                )
                .url("http://example.com/video")
                .playerId("playerId")
                .segmentStartSeconds(10)
                .segmentEndSeconds(20)
                .videoDurationSeconds(60)
                .videoIndex(10)
                .videoId("123")
                .playbackDevice("device-id")
                .build()

        val document = EventToDocumentConverter.convertVideoSegmentPlayed(videoSegmentPlayed = event)
        assertThat(document.getString("type")).isEqualTo("VIDEO_SEGMENT_PLAYED")
        assertThat(document.getString("userId")).isEqualTo("user-1")
        assertThat(document.getBoolean("userIsBoclips")).isEqualTo(true)
        assertThat(document.getDate("timestamp").toInstant().atZone(ZoneOffset.UTC)).isEqualTo(ZonedDateTime.of(2019, 5, 31, 13, 45, 59, 0, ZoneOffset.UTC))
        assertThat(document.getString("url")).isEqualTo("http://example.com/video")
        assertThat(document.getString("playerId")).isEqualTo("playerId")
        assertThat(document.getLong("segmentStartSeconds")).isEqualTo(10)
        assertThat(document.getLong("segmentEndSeconds")).isEqualTo(20)
        assertThat(document.getLong("videoDurationSeconds")).isEqualTo(60)
        assertThat(document.getInteger("videoIndex")).isEqualTo(10)
        assertThat(document.getString("videoId")).isEqualTo("123")
        assertThat(document.getString("playbackDevice")).isEqualTo("device-id")
    }

    @Test
    fun videoPlayerInteractedWith() {
        val event = TestFactories.createVideoPlayerInteractedWith(
                videoId = "video-id",
                playerId = "player-id",
                videoDurationSeconds = 50,
                currentTime = 34,
                subtype = "captions-on",
                payload = mapOf<String, Any>(
                        Pair("kind", "caption-kind"),
                        Pair("language", "caption-language"),
                        Pair("id", "caption-id"),
                        Pair("label", "caption-label")
                )
        )

        val document = EventToDocumentConverter.convertVideoPlayerInteractedWith(event)
        assertThat(document.getString("type")).isEqualTo("VIDEO_PLAYER_INTERACTED_WITH")
        assertThat(document.getString("userId")).isEqualTo("user-1")
        assertThat(document.getString("playerId")).isEqualTo("player-id")
        assertThat(document.getString("videoId")).isEqualTo("video-id")
        assertThat(document.getLong("videoDurationSeconds")).isEqualTo(50)
        assertThat(document.getLong("currentTime")).isEqualTo(34)
        assertThat(document.getString("subtype")).isEqualTo("captions-on")
        assertThat(document["payload"]).isEqualTo(event.payload)
    }

    @Test
    fun convertVideoAddedToCollection() {
        val event = TestFactories.createVideoAddedToCollection(videoId = "video-id", collectionId = "collection-id")

        val document = EventToDocumentConverter.convertVideoAddedToCollection(event)

        assertThat(document.getString("type")).isEqualTo("VIDEO_ADDED_TO_COLLECTION")
        assertThat(document.getString("videoId")).isEqualTo("video-id")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
    }

    @Test
    fun convertVideoRemovedFromCollection() {
        val event = TestFactories.createVideoRemovedFromCollection(videoId = "video-id", collectionId = "collection-id")

        val document = EventToDocumentConverter.convertVideoRemovedFromCollection(event)

        assertThat(document.getString("type")).isEqualTo("VIDEO_REMOVED_FROM_COLLECTION")
        assertThat(document.getString("videoId")).isEqualTo("video-id")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
    }

    @Test
    fun `convertCollectionBookmarkChanged bookmarking`() {
        val event = TestFactories.createCollectionBookmarkChanged(collectionId = "collection-id", isBookmarked = true)

        val document = EventToDocumentConverter.convertCollectionBookmarkChanged(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_BOOKMARK_CHANGED")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
        assertThat(document.getBoolean("isBookmarked")).isTrue()
    }

    @Test
    fun `convertCollectionBookmarkChanged unbookmarking`() {
        val event = TestFactories.createCollectionBookmarkChanged(collectionId = "collection-id", isBookmarked = false)

        val document = EventToDocumentConverter.convertCollectionBookmarkChanged(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_BOOKMARK_CHANGED")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
        assertThat(document.getBoolean("isBookmarked")).isFalse()
    }

    @Test
    fun `convertCollectionVisibilityChanged made public`() {
        val event = TestFactories.createCollectionVisibilityChanged(collectionId = "collection-id", isPublic = true)

        val document = EventToDocumentConverter.convertCollectionVisibilityChanged(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_VISIBILITY_CHANGED")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
        assertThat(document.getBoolean("isPublic")).isTrue()
    }

    @Test
    fun `convertCollectionVisibilityChanged made private`() {
        val event = TestFactories.createCollectionVisibilityChanged(collectionId = "collection-id", isPublic = false)

        val document = EventToDocumentConverter.convertCollectionVisibilityChanged(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_VISIBILITY_CHANGED")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
        assertThat(document.getBoolean("isPublic")).isFalse()
    }

    @Test
    fun convertCollectionSubjectsChanged() {
        val event = TestFactories.createCollectionSubjectsChanged(collectionId = "collection-id", subjects = setOf("Science"))

        val document = EventToDocumentConverter.convertCollectionSubjectsChanged(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_SUBJECTS_CHANGED")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
        assertThat(document.getList("subjects", String::class.java)).containsExactly("Science")
    }

    @Test
    fun convertCollectionAgeRangeChanged() {
        val event = TestFactories.createCollectionAgeRangeChanged(collectionId = "collection-1", rangeMin = 5, rangeMax = 19)

        val document = EventToDocumentConverter.convertCollectionAgeRangeChanged(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_AGE_RANGE_CHANGED")
        assertThat(document.getString("collectionId")).isEqualTo("collection-1")
        assertThat(document.getInteger("rangeMin")).isEqualTo(5)
        assertThat(document.getInteger("rangeMax")).isEqualTo(19)
    }

    @Test
    fun convertCollectionAgeRangeChanged_whenRangeMaxNull() {
        val event = TestFactories.createCollectionAgeRangeChanged(collectionId = "collection-1", rangeMin = 5, rangeMax = null)

        val document = EventToDocumentConverter.convertCollectionAgeRangeChanged(event)

        assertThat(document.get("rangeMax")).isNull()
    }
}


