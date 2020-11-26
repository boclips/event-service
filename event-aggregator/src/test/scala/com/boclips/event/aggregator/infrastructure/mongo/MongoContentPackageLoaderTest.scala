package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.infrastructure.contentpackage.ContentPackageDocument

class MongoContentPackageLoaderTest extends IntegrationTest {

  "load" should "read content packages" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.collection[ContentPackageDocument]("content-packages")
    collection insertOne ContentPackageDocument.sample.build()

    val contentPackages = new MongoContentPackageLoader(mongo).load()(spark).collect()

    contentPackages should have length 1
  }
}
