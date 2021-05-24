package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.orders.Order
import com.boclips.event.aggregator.domain.service.OrderLoader
import com.boclips.event.infrastructure.order.OrderDocument
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoOrderLoader(private val mongoClient: SparkMongoClient) extends OrderLoader {

  override def load()(implicit session: SparkSession): RDD[Order] = {
    mongoClient
      .collectionRDD[OrderDocument]("orders")
      .filter(
        doc =>
          doc.getStatus == "DELIVERED"
          || doc.getStatus == "READY"
          || ( doc.getStatus == "INCOMPLETED" && doc.getOrderSource == "BOCLIPS")
      )
      .map(DocumentToOrderConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Orders")
  }
}
