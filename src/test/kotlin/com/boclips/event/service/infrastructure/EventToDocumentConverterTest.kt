package com.boclips.event.service.infrastructure

import com.boclips.event.service.testsupport.TestFactories
import com.boclips.events.types.User
import com.boclips.events.types.UserActivated
import com.boclips.events.types.video.VideoSegmentPlayed
import com.boclips.events.types.video.VideosSearched
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class EventToDocumentConverterTest {

    @Test
    fun `userIsBoclips marked true for boclips email addresses`() {
        val event = TestFactories.createUserActivated(userEmail = "david@boclips.com")

        val document = EventToDocumentConverter.convertUserActivated(event)

        assertThat(document.getBoolean("userIsBoclips")).isTrue()
    }

    @Test
    fun userActivated() {
        val event = UserActivated.builder()
                .timestamp(Date.from(ZonedDateTime.parse("2018-05-31T13:45:59Z").toInstant()))
                .user(User.builder()
                        .id("user-1")
                        .email("someone@gmail.com")
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
                        .email("someone@gmail.com")
                        .build()
                )
                .url("http://example.com/hello")
                .pageIndex(5)
                .pageSize(10)
                .query("hello")
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
    }

    @Test
    fun videoSegmentPlayed() {
        val event = VideoSegmentPlayed.builder()
                .timestamp(Date.from(ZonedDateTime.parse("2019-05-31T13:45:59Z").toInstant()))
                .user(User.builder()
                        .id("user-1")
                        .email("someone@boclips.com")
                        .build()
                )
                .url("http://example.com/video")
                .playerId("playerId")
                .segmentStartSeconds(10)
                .segmentEndSeconds(20)
                .videoDurationSeconds(60)
                .videoIndex(10)
                .videoId("123")
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
    fun convertCollectionBookmarked() {
        val event = TestFactories.createCollectionBookmarked(collectionId = "collection-id")

        val document = EventToDocumentConverter.convertCollectionBookmarked(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_BOOKMARKED")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
    }

    @Test
    fun convertCollectionUnbookmarked() {
        val event = TestFactories.createCollectionUnbookmarked(collectionId = "collection-id")

        val document = EventToDocumentConverter.convertCollectionUnbookmarked(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_UNBOOKMARKED")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
    }

    @Test
    fun convertCollectionMadePrivate() {
        val event = TestFactories.createCollectionMadePrivate(collectionId = "collection-id")

        val document = EventToDocumentConverter.convertCollectionMadePrivate(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_MADE_PRIVATE")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
    }

    @Test
    fun convertCollectionMadePublic() {
        val event = TestFactories.createCollectionMadePublic(collectionId = "collection-id")

        val document = EventToDocumentConverter.convertCollectionMadePublic(event)

        assertThat(document.getString("type")).isEqualTo("COLLECTION_MADE_PUBLIC")
        assertThat(document.getString("collectionId")).isEqualTo("collection-id")
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


