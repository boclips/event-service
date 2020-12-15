package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.infrastructure.order.OrderDocument
import org.apache.spark.sql.SparkSession

class MongoOrderLoaderTest extends IntegrationTest {

  it should "load orders with status READY and DELIVERED" in mongoSparkTest { (spark: SparkSession, mongo) =>
    val collection = mongo.collection[OrderDocument]("orders")
    collection insertOne OrderDocument.sample.id("order-1").status("READY").build()
    collection insertOne OrderDocument.sample.id("order-2").status("CANCELLED").build()
    collection insertOne OrderDocument.sample.id("order-3").status("DELIVERED").build()

    val orders = new MongoOrderLoader(mongo).load()(spark).toLocalIterator.toList
    val orderIds = orders.map(_.id.value)

    orders should have size 2
    orderIds should contain ("order-1")
    orderIds should contain ("order-3")
  }

}
