package com.boclips.event.service.application

import com.boclips.event.service.domain.CollectionRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.events.collection.CollectionCreated
import com.boclips.eventbus.events.collection.CollectionDeleted
import com.boclips.eventbus.events.collection.CollectionUpdated

class UpdateCollection(private val collectionRepository: CollectionRepository) {

    @BoclipsEventListener
    fun collectionCreated(event: CollectionCreated) {
        collectionRepository.saveCollection(event.collection)
    }

    @BoclipsEventListener
    fun collectionUpdated(event: CollectionUpdated) {
        collectionRepository.saveCollection(event.collection)
    }

    @BoclipsEventListener
    fun collectionDeleted(event: CollectionDeleted) {
        collectionRepository.markDeleted(event.collectionId)
    }


}
