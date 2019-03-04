package com.boclips.event.service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "mixpanel")
data class MixpanelProperties(
    var projectToken: String = ""
)
