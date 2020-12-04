package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.contentpackages.{ContentPackage, ContentPackageId}
import com.boclips.event.infrastructure.contentpackage.ContentPackageDocument

object DocumentToContentPackageConverter {
  def convert(document: ContentPackageDocument): ContentPackage =
    ContentPackage(
      id = ContentPackageId(document.getId),
      name = document.getName
    )
}
