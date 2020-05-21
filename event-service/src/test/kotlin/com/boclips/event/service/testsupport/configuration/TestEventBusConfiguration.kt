package com.boclips.event.service.testsupport.configuration

import com.boclips.eventbus.infrastructure.SynchronousFakeEventBus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestEventBusConfiguration {

    @Bean
    fun testEventBus(): SynchronousFakeEventBus {
        return SynchronousFakeEventBus()
    }
}
