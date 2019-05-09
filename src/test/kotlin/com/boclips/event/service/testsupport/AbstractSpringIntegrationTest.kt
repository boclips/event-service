package com.boclips.event.service.testsupport

import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.MongodProcess
import mu.KLogging
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@ActiveProfiles("test", "fake-mixpanel")
abstract class AbstractSpringIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mongoClient: MongoClient

    companion object : KLogging() {
        private var mongoProcess: MongodProcess? = null

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            if (mongoProcess == null) {
                mongoProcess = TestMongoProcess.process
            }
        }
    }

    @BeforeEach
    fun resetState() {
        mongoClient.apply {
            listDatabaseNames()
                    .filterNot { setOf("admin", "config").contains(it) }
                    .forEach {
                        println("Dropping $it")
                        dropDatabase(it)
                    }
        }

    }
}
