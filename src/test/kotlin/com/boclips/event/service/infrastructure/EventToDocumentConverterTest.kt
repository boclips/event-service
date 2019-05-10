package com.boclips.event.service.infrastructure

import com.boclips.event.service.testsupport.TestFactories
import com.boclips.events.types.User
import com.boclips.events.types.UserActivated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class EventToDocumentConverterTest {

    @Test
    fun `userIsBoclips marked true for boclips email addresses`() {
        val event = TestFactories.createUserActivated(userEmail = "david@boclips.com")

        val document = EventToDocumentConverter.convertUserActivated(event)

        assertThat(document.getBoolean("userIsBoclips")).isTrue()
    }

    @Test
    fun userActivated() {
        val event = UserActivated.builder()
                .timestamp(Date.from(ZonedDateTime.parse("2018-05-31T13:45:59Z").toInstant()))
                .user(User.builder()
                        .id("user-1")
                        .email("someone@gmail.com")
                        .build()
                )
                .totalUsers(100)
                .activatedUsers(50)
                .build()

        val document = EventToDocumentConverter.convertUserActivated(event)

        assertThat(document.getString("type")).isEqualTo("USER_ACTIVATED")
        assertThat(document.getString("userId")).isEqualTo("user-1")
        assertThat(document.getBoolean("userIsBoclips")).isFalse()
        assertThat(document.getDate("timestamp").toInstant().atZone(ZoneOffset.UTC)).isEqualTo(ZonedDateTime.of(2018, 5, 31, 13, 45, 59, 0, ZoneOffset.UTC))
        assertThat(document.getLong("totalUsers")).isEqualTo(100)
        assertThat(document.getLong("activatedUsers")).isEqualTo(50)
    }
}


