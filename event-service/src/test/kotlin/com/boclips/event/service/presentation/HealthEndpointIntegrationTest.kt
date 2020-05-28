package com.boclips.event.service.presentation

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class HealthEndpointIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `health returns 200 OK for unauthenticated requests`() {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk)
    }
}
