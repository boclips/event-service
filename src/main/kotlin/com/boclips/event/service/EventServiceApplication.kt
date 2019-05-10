package com.boclips.event.service

import com.boclips.events.spring.EnableBoclipsEvents
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBoclipsEvents(appName = "event-service")
class EventServiceApplication

fun main(args: Array<String>) {
	runApplication<EventServiceApplication>(*args)
}
