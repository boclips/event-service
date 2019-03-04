package com.boclips.event.service.presentation

import com.boclips.event.service.application.TrackEvent
import com.boclips.event.service.domain.model.Event
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/events")
class EventsController(val trackEvent: TrackEvent) {

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping
    fun postEvent(@RequestBody event: Event) {
        trackEvent(event)
    }
}