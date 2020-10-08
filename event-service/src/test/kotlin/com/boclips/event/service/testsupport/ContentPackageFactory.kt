package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.contentpackage.ContentPackage
import com.boclips.eventbus.domain.contentpackage.ContentPackageId

object ContentPackageFactory {
    fun createContentPackage(
        id: String = "content-package-id",
        name: String = "content package name"
    ) =
        ContentPackage.builder()
            .id(
                ContentPackageId.builder()
                    .value(id)
                    .build()
            )
            .name(name)
            .build()
}
