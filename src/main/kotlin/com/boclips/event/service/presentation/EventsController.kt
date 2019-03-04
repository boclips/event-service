package com.boclips.event.service.presentation

import com.boclips.event.service.application.TrackEvent
import com.boclips.event.service.domain.model.Event
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.UnsupportedOperationException

@RestController
@RequestMapping("/v1/events")
class EventsController(val trackEvent: TrackEvent) {

    companion object {
        fun eventsLink() = linkTo(methodOn(EventsController::class.java).postEvent(null)).withRel("events")
    }

    @PostMapping
    fun postEvent(@RequestBody event: Event?): ResponseEntity<Void> {
        event ?: throw UnsupportedOperationException()
        trackEvent(event)
        return ResponseEntity(HttpHeaders(), HttpStatus.ACCEPTED)
    }
}
