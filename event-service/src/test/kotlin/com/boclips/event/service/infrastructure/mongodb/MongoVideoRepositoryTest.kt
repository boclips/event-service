package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.video.VideoDocument
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.VideoFactory.createVideo
import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.video.Dimensions
import com.boclips.eventbus.domain.video.PlaybackProviderType
import com.boclips.eventbus.domain.video.VideoAsset
import com.boclips.eventbus.domain.video.VideoType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime

class MongoVideoRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var videoRepository: MongoVideoRepository

    @Test
    fun `creating a video`() {
        videoRepository.saveVideo(
            createVideo(
                id = "1234",
                title = "the title",
                channelId = "channel id",
                playbackProviderType = PlaybackProviderType.YOUTUBE,
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
                )
            )
        )

        val document = document()
        assertThat(document.id).isEqualTo("1234")
        assertThat(document.title).isEqualTo("the title")
        assertThat(document.channelId).isEqualTo("channel id")
        assertThat(document.playbackProviderType).isEqualTo("YOUTUBE")
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

        val rawDocument = mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(MongoVideoRepository.COLLECTION_NAME)
            .find().toList().single()

        assertThat(rawDocument.get("_id")).isEqualTo("v1")
    }

    private fun document(): VideoDocument {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<VideoDocument>(MongoVideoRepository.COLLECTION_NAME, VideoDocument::class.java)
            .find().toList().single()
    }
}
