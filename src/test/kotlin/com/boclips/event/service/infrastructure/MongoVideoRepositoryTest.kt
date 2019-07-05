package com.boclips.event.service.infrastructure

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MongoVideoRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var videoRepository: MongoVideoRepository

    @Test
    fun `creating a video`() {
        videoRepository.saveVideo(id = "1234", title = "the title", contentPartnerName = "the content partner")

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("1234")
        assertThat(document.getString("title")).isEqualTo("the title")
        assertThat(document.getString("contentPartnerName")).isEqualTo("the content partner")
    }

    @Test
    fun `updating a video`() {
        videoRepository.saveVideo(id = "1234", title = "the title", contentPartnerName = "the content partner")
        videoRepository.saveVideo(id = "1234", title = "the updated title", contentPartnerName = "the updated content partner")

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("1234")
        assertThat(document.getString("title")).isEqualTo("the updated title")
        assertThat(document.getString("contentPartnerName")).isEqualTo("the updated content partner")
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection("videos").find().toList().single()
    }
}
