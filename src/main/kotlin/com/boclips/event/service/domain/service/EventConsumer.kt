package com.boclips.event.service.domain.service

import com.boclips.event.service.domain.model.Event

interface EventConsumer {

    fun consumeEvent(events: List<Event>)
}