package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.order.OrderDocument
import org.apache.spark.sql.SparkSession

class MongoOrderLoaderTest extends IntegrationTest {

  it should "load orders with status COMPLETED" in mongoSparkTest { (spark: SparkSession, mongo) =>
    val collection = mongo.db.getCollection("orders", classOf[OrderDocument])
    collection insertOne OrderDocument.sample._id("order-1").status("COMPLETED").build()
    collection insertOne OrderDocument.sample._id("order-2").status("CANCELLED").build()

    val orders = new MongoOrderLoader(spark).load().toLocalIterator.toList

    orders should have size 1
  }

}
