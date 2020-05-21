package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Collection
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.CollectionFactory.createCollectionDocument
import org.apache.spark.sql.SparkSession

class MongoCollectionLoaderIntegrationTest extends IntegrationTest {

  "load" should "read collections" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.db getCollection "collections"
    collection insertOne createCollectionDocument()

    val collections = loadCollection(spark)

    collections should have length 1
  }

  private def loadCollection(session: SparkSession): Array[Collection] = new MongoCollectionLoader(session).load.collect()

}
