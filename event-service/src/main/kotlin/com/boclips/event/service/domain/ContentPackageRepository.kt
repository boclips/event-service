package com.boclips.event.service.domain

import com.boclips.eventbus.domain.contentpackage.ContentPackage

interface ContentPackageRepository {
    fun save(contentPackage: ContentPackage)
}
