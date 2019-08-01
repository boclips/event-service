package com.boclips.event.service.infrastructure

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.video.PlaybackProviderType
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import com.boclips.event.service.testsupport.TestFactories.createVideo as createVideo

class MongoVideoRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var videoRepository: MongoVideoRepository

    @Test
    fun `creating a video`() {
        videoRepository.saveVideo(createVideo(
                id = "1234",
                title = "the title",
                contentPartnerName = "the content partner",
                playbackProviderType = PlaybackProviderType.YOUTUBE,
                subjectNames = listOf("Maths"),
                ageRange = AgeRange(5, 11)
        ))

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("1234")
        assertThat(document.getString("title")).isEqualTo("the title")
        assertThat(document.getString("contentPartnerName")).isEqualTo("the content partner")
        assertThat(document.getString("playbackProviderType")).isEqualTo("YOUTUBE")
        assertThat(document.getList("subjects", String::class.java)).containsExactly("Maths")
        assertThat(document.getInteger("ageRangeMin")).isEqualTo(5)
        assertThat(document.getInteger("ageRangeMax")).isEqualTo(11)
    }

    @Test
    fun `updating a video`() {
        videoRepository.saveVideo(createVideo(id = "1234", title = "the title", contentPartnerName = "the content partner", subjectNames = emptyList(), ageRange = AgeRange()))
        videoRepository.saveVideo(createVideo(id = "1234", title = "the updated title", contentPartnerName = "the updated content partner", subjectNames = listOf("English"), ageRange = AgeRange(3, 7)))

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("1234")
        assertThat(document.getString("title")).isEqualTo("the updated title")
        assertThat(document.getString("contentPartnerName")).isEqualTo("the updated content partner")
        assertThat(document.getList("subjects", String::class.java)).containsExactly("English")
        assertThat(document.getInteger("ageRangeMin")).isEqualTo(3)
        assertThat(document.getInteger("ageRangeMax")).isEqualTo(7)
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection("videos").find().toList().single()
    }
}
