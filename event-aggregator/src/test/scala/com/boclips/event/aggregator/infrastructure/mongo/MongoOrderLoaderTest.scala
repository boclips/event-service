package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.infrastructure.order.OrderDocument
import org.apache.spark.sql.SparkSession

class MongoOrderLoaderTest extends IntegrationTest {

  it should "load orders with status COMPLETED" in mongoSparkTest { (spark: SparkSession, mongo) =>
    val collection = mongo.collection[OrderDocument]("orders")
    collection insertOne OrderDocument.sample._id("order-1").status("COMPLETED").build()
    collection insertOne OrderDocument.sample._id("order-2").status("CANCELLED").build()

    val orders = new MongoOrderLoader(mongo).load()(spark).toLocalIterator.toList

    orders should have size 1
  }

}
