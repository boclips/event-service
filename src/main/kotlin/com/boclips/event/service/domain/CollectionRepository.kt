package com.boclips.event.service.domain

import com.boclips.eventbus.domain.collection.Collection

interface CollectionRepository {
    fun saveCollection(collection: Collection)

    fun markDeleted(collectionId: String)
}
