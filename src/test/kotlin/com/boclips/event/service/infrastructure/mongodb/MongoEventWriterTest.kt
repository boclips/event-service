package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MongoEventWriterTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var mongoEventWriter: MongoEventWriter

    @Test
    fun writeEvent() {
        mongoEventWriter.write(mapOf("type" to "SEARCH", "query" to "bla"))

        assertThat(eventDocument()["type"]).isEqualTo("SEARCH")
        assertThat(eventDocument()["query"]).isEqualTo("bla")
    }

    fun eventDocument() = document(MongoEventWriter.COLLECTION_NAME)
}
