package com.boclips.event.service.domain

interface EventWriter {
    fun write(event: Map<String, Any>)
}
