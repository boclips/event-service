package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.infrastructure.collection.CollectionDocument

class MongoCollectionLoaderIntegrationTest extends IntegrationTest {

  "load" should "read collections" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.collection[CollectionDocument]("collections")
    collection insertOne CollectionDocument.sample.build()

    val collections = new MongoCollectionLoader(mongo).load()(spark).collect()

    collections should have length 1
  }
}
