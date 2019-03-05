package com.boclips.event.service.presentation

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import org.hamcrest.Matchers
import org.hamcrest.Matchers.endsWith
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class LinksControllerTest : AbstractSpringIntegrationTest() {

    @Test
    fun `GET links includes the events resource`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.events.href", endsWith("/v1/events")))
    }
}