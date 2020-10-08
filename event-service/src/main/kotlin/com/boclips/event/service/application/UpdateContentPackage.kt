package com.boclips.event.service.application

import com.boclips.event.service.domain.ContentPackageRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.domain.contentpackage.ContentPackage
import com.boclips.eventbus.events.contentpackage.ContentPackageBroadcastRequested
import com.boclips.eventbus.events.contentpackage.ContentPackageUpdated

class UpdateContentPackage(private val repository: ContentPackageRepository) {
    @BoclipsEventListener
    fun contentPackageUpdate(event: ContentPackageUpdated) {
        save(event.contentPackage)
    }

    @BoclipsEventListener
    fun contentPackageBroadcastRequested(event: ContentPackageBroadcastRequested) {
        save(event.contentPackage)
    }

    private fun save(contentPackage: ContentPackage) {
        repository.save(contentPackage)
    }
}
