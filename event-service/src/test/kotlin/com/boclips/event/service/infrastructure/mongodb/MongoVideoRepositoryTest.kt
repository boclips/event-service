package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.video.VideoDocument
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.CategoryFactory
import com.boclips.event.service.testsupport.CategoryFactory.createCategoryWithAncestors
import com.boclips.event.service.testsupport.VideoFactory.createVideo
import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.category.CategoryWithAncestors
import com.boclips.eventbus.domain.video.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class MongoVideoRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var videoRepository: MongoVideoRepository

    @Test
    fun `creating a video`() {
        videoRepository.saveVideo(
            createVideo(
                id = "1234",
                title = "the title",
                description = "the description",
                channelId = "channel id",
                playbackProviderType = PlaybackProviderType.YOUTUBE,
                playbackId = "playback id",
                subjectNames = listOf("Maths"),
                ageRange = AgeRange(5, 11),
                type = VideoType.NEWS,
                ingestedAt = ZonedDateTime.of(2019, 11, 18, 12, 13, 14, 150000000, ZoneOffset.UTC),
                releasedOn = LocalDate.of(1939, 9, 1),
                durationSeconds = 60,
                originalDimensions = Dimensions(480, 320),
                assets = listOf(
                    VideoAsset
                        .builder()
                        .dimensions(Dimensions(420, 320))
                        .sizeKb(1024)
                        .id("my-id")
                        .bitrateKbps(128)
                        .build()
                ),
                promoted = true,
                topics = listOf(
                    VideoTopic.builder()
                        .name("topic")
                        .confidence(0.5)
                        .language(Locale.FRENCH)
                        .parent(
                            VideoTopic.builder()
                                .name("topic-parent")
                                .confidence(0.76)
                                .language(Locale.ENGLISH)
                                .parent(
                                    VideoTopic.builder()
                                        .name("topic-grandparent")
                                        .confidence(0.2)
                                        .language(Locale.ITALIAN)
                                        .build()
                                )
                                .build()
                        ).build()
                ),
                keywords = listOf("key", "words", "are", "cool"),
                sourceVideoReference = "video-reference",
                deactivated = true,
                categories = mutableMapOf(VideoCategorySource.MANUAL to mutableSetOf(createCategoryWithAncestors(code = "BBB", description = "Bats", ancestors = setOf("Mammals"))))
            )
        )

        val document = document()
        assertThat(document.id).isEqualTo("1234")
        assertThat(document.title).isEqualTo("the title")
        assertThat(document.description).isEqualTo("the description")
        assertThat(document.channelId).isEqualTo("channel id")
        assertThat(document.playbackProviderType).isEqualTo("YOUTUBE")
        assertThat(document.playbackId).isEqualTo("playback id")
        assertThat(document.subjects).containsExactly("Maths")
        assertThat(document.ageRangeMin).isEqualTo(5)
        assertThat(document.ageRangeMax).isEqualTo(11)
        assertThat(document.type).isEqualTo("NEWS")
        assertThat(document.durationSeconds).isEqualTo(60)
        assertThat(document.ingestedAt).isEqualTo("2019-11-18T12:13:14.15Z")
        assertThat(document.releasedOn).isEqualTo("1939-09-01")
        assertThat(document.originalWidth).isEqualTo(480)
        assertThat(document.originalHeight).isEqualTo(320)
        assertThat(document.assets).hasSize(1)
        assertThat(document.assets.first().id).isEqualTo("my-id")
        assertThat(document.assets.first().width).isEqualTo(420)
        assertThat(document.assets.first().height).isEqualTo(320)
        assertThat(document.assets.first().bitrateKbps).isEqualTo(128)
        assertThat(document.assets.first().sizeKb).isEqualTo(1024)
        assertThat(document.promoted).isEqualTo(true)
        assertThat(document.keywords).containsExactly("key", "words", "are", "cool")
        assertThat(document.sourceVideoReference).isEqualTo("video-reference")
        assertThat(document.deactivated).isEqualTo(true)
        assertThat(document.categories).isNotNull
        assertThat(document.categories["MANUAL"]!!.map { it.code.toString() }[0]).isEqualTo("BBB")


        val firstTopic = document.topics.first()
        assertThat(firstTopic.name).isEqualTo("topic")
        assertThat(firstTopic.confidence).isEqualTo(0.5)
        assertThat(firstTopic.language).isEqualTo("fr")
        val parentTopic = firstTopic.parent
        assertNotNull(parentTopic)
        assertThat(parentTopic.name).isEqualTo("topic-parent")
        assertThat(parentTopic.confidence).isEqualTo(0.76)
        assertThat(parentTopic.language).isEqualTo("en")
        val grandparentTopic = parentTopic.parent
        assertNotNull(grandparentTopic)
        assertThat(grandparentTopic.name).isEqualTo("topic-grandparent")
        assertThat(grandparentTopic.confidence).isEqualTo(0.2)
        assertThat(grandparentTopic.language).isEqualTo("it")
    }

    @Test
    fun `creating a video when original dimensions are null`() {
        videoRepository.saveVideo(
            createVideo(
                originalDimensions = null
            )
        )

        val document = document()
        assertNull(document.originalWidth)
        assertNull(document.originalHeight)
    }

    @Test
    fun `creating when deactivated is null`() {
        videoRepository.saveVideo(
            createVideo(
                deactivated = null
            )
        )

        val document = document()
        assertNull(document.deactivated)
    }

    @Test
    fun `creating a video when assets are null`() {
        videoRepository.saveVideo(
            createVideo(
                assets = null
            )
        )

        val document = document()
        assertThat(document.assets).isNull()
    }

    @Test
    fun `creating a video when categories are empty`() {
        videoRepository.saveVideo(
            createVideo(
                categories = Collections.emptyMap()
        )
        )

        val document = document()
        assertThat(document.categories).isEmpty()
    }

    @Test
    fun `updating a video`() {
        videoRepository.saveVideo(
            createVideo(
                id = "1234",
                title = "the title",
                channelId = "the channel id",
                subjectNames = emptyList(),
                ageRange = AgeRange()
            )
        )
        videoRepository.saveVideo(
            createVideo(
                id = "1234",
                title = "the updated title",
                channelId = "the updated channel id",
                subjectNames = listOf("English"),
                ageRange = AgeRange(3, 7)
            )
        )

        val document = document()
        assertThat(document.id).isEqualTo("1234")
        assertThat(document.title).isEqualTo("the updated title")
        assertThat(document.channelId).isEqualTo("the updated channel id")
        assertThat(document.subjects).containsExactly("English")
        assertThat(document.ageRangeMin).isEqualTo(3)
        assertThat(document.ageRangeMax).isEqualTo(7)
    }

    @Test
    fun `video ids are used as document ids`() {
        videoRepository.saveVideo(createVideo(id = "v1"))

        val rawDocument =
            mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(MongoVideoRepository.COLLECTION_NAME)
                .find().toList().single()

        assertThat(rawDocument.get("_id")).isEqualTo("v1")
    }

    private fun document(): VideoDocument {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME)
            .getCollection(MongoVideoRepository.COLLECTION_NAME, VideoDocument::class.java)
            .find().toList().single()
    }
}
