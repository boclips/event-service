package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createVideo
import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.video.Dimensions
import com.boclips.eventbus.domain.video.PlaybackProviderType
import com.boclips.eventbus.domain.video.VideoAsset
import com.boclips.eventbus.domain.video.VideoType
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
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
                contentPartnerName = "the content partner",
                playbackProviderType = PlaybackProviderType.YOUTUBE,
                subjectNames = listOf("Maths"),
                ageRange = AgeRange(5, 11),
                type = VideoType.NEWS,
                ingestedAt = ZonedDateTime.of(2019, 11, 18, 12, 13, 14, 150000000, ZoneOffset.UTC),
                ingestedOn = LocalDate.ofYearDay(2019, 32),
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
        assertThat(document.getString("_id")).isEqualTo("1234")
        assertThat(document.getString("title")).isEqualTo("the title")
        assertThat(document.getString("contentPartnerName")).isEqualTo("the content partner")
        assertThat(document.getString("playbackProviderType")).isEqualTo("YOUTUBE")
        assertThat(document.getList("subjects", String::class.java)).containsExactly("Maths")
        assertThat(document.getInteger("ageRangeMin")).isEqualTo(5)
        assertThat(document.getInteger("ageRangeMax")).isEqualTo(11)
        assertThat(document.getString("type")).isEqualTo("NEWS")
        assertThat(document.getInteger("durationSeconds")).isEqualTo(60)
        assertThat(document.getString("ingestedAt")).isEqualTo("2019-11-18T12:13:14.15Z")
        assertThat(document.getString("ingestedOn")).isEqualTo("2019-02-01")
        assertThat(document.getInteger("originalWidth")).isEqualTo(480)
        assertThat(document.getInteger("originalHeight")).isEqualTo(320)
        assertThat(document.getList("assets", Map::class.java)).hasSize(1)
        assertThat(document.getList("assets", Map::class.java)[0]["id"]).isEqualTo("my-id")
        assertThat(document.getList("assets", Map::class.java)[0]["width"]).isEqualTo(420)
        assertThat(document.getList("assets", Map::class.java)[0]["height"]).isEqualTo(320)
        assertThat(document.getList("assets", Map::class.java)[0]["bitrateKbps"]).isEqualTo(128)
        assertThat(document.getList("assets", Map::class.java)[0]["sizeKb"]).isEqualTo(1024)
    }

    @Test
    fun `creating a video when ingestedAt is null`() {
        videoRepository.saveVideo(createVideo(ingestedAt = null))

        val document = document()

        assertThat(document["ingestedAt"]).isNull()
    }

    @Test
    fun `creating a video when original dimensions are null`() {
        videoRepository.saveVideo(
            createVideo(
                originalDimensions = null
            )
        )

        val document = document()
        assertThat(document.get("originalWidth")).isNull()
        assertThat(document.get("originalHeight")).isNull()
    }

    @Test
    fun `creating a video when assets are null`() {
        videoRepository.saveVideo(
            createVideo(
                assets = null
            )
        )

        val document = document()
        assertThat(document.get("assets")).isNull()
    }

    @Test
    fun `updating a video`() {
        videoRepository.saveVideo(
            createVideo(
                id = "1234",
                title = "the title",
                contentPartnerName = "the content partner",
                subjectNames = emptyList(),
                ageRange = AgeRange()
            )
        )
        videoRepository.saveVideo(
            createVideo(
                id = "1234",
                title = "the updated title",
                contentPartnerName = "the updated content partner",
                subjectNames = listOf("English"),
                ageRange = AgeRange(3, 7)
            )
        )

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("1234")
        assertThat(document.getString("title")).isEqualTo("the updated title")
        assertThat(document.getString("contentPartnerName")).isEqualTo("the updated content partner")
        assertThat(document.getList("subjects", String::class.java)).containsExactly("English")
        assertThat(document.getInteger("ageRangeMin")).isEqualTo(3)
        assertThat(document.getInteger("ageRangeMax")).isEqualTo(7)
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(MongoVideoRepository.COLLECTION_NAME)
            .find().toList().single()
    }
}
