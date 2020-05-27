package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Order
import com.boclips.event.aggregator.domain.service.OrderLoader
import com.order.OrderDocument
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoOrderLoader(private val mongoClient: SparkMongoClient) extends OrderLoader {

  override def load()(implicit session: SparkSession): RDD[Order] = {
    mongoClient
      .collectionRDD[OrderDocument]("orders")
      .filter(_.getStatus == "COMPLETED")
      .map(DocumentToOrderConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Orders")
  }
}