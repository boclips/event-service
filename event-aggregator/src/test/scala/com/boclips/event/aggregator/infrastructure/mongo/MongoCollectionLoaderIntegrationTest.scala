package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.CollectionFactory.createCollectionDocument

class MongoCollectionLoaderIntegrationTest extends IntegrationTest {

  "load" should "read collections" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo collection "collections"
    collection insertOne createCollectionDocument()

    val collections = new MongoCollectionLoader(mongo).load()(spark).collect()

    collections should have length 1
  }
}
