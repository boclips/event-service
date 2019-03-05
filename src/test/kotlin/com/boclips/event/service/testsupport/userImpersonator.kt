package com.boclips.event.service.testsupport

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

fun MockHttpServletRequestBuilder.asUser(identifier: String = "user@example.com") =
        this.with(SecurityMockMvcRequestPostProcessors.user(identifier).roles(""))