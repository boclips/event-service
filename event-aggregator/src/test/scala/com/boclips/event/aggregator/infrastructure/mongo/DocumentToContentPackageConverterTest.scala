package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.contentpackages.ContentPackageId
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.contentpackage.ContentPackageDocument

class DocumentToContentPackageConverterTest extends Test {

  it should "convert from document to content package" in {
    val document = ContentPackageDocument.sample.id("id").name("name").build()
    val contentPackage = DocumentToContentPackageConverter convert document
    contentPackage.id should be (ContentPackageId("id"))
    contentPackage.name should be ("name")
  }

}
