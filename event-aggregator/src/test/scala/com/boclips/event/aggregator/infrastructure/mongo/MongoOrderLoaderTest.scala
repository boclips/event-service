package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.OrderFactory.createOrderDocument
import org.apache.spark.sql.SparkSession

class MongoOrderLoaderTest extends IntegrationTest {

  it should "load orders with status COMPLETED" in mongoSparkTest { (spark: SparkSession, mongo) =>
    val collection = mongo.db getCollection "orders"
    collection insertOne createOrderDocument(id = "order-1", status = "COMPLETED")
    collection insertOne createOrderDocument(id = "order-2", status = "CANCELLED")

    val orders = new MongoOrderLoader(spark).load().toLocalIterator.toList

    orders should have size 1
  }

}
