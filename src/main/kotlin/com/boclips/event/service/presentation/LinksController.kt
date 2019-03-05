package com.boclips.event.service.presentation

import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class LinksController {

    @GetMapping
    fun getLinks(): Resource<String> {
        return Resource("", listOf(EventsController.eventsLink()))
    }

}