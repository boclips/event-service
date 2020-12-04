package com.boclips.event.aggregator.domain.model.contentpackages

case class ContentPackageId(value: String)

case class ContentPackage(
                           id: ContentPackageId,
                           name: String
                         )
