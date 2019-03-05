package com.boclips.event.service.config

import com.boclips.security.EnableBoclipsSecurity
import com.boclips.security.HttpSecurityConfigurer
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.stereotype.Component

@Profile("!test")
@Configuration
@EnableBoclipsSecurity
class WebSecurityConfig

@Component
class EventServiceHttpSecurityConfigurer : HttpSecurityConfigurer {
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/actuator/health").permitAll()

                .antMatchers(HttpMethod.GET, "/v1").permitAll()
                .antMatchers(HttpMethod.GET, "/v1/").permitAll()

                .antMatchers(HttpMethod.POST, "/v1/events").permitAll()
                .anyRequest().denyAll()
    }
}


