package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Order
import com.boclips.event.aggregator.domain.service.OrderLoader
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoOrderLoader(private val session: SparkSession) extends OrderLoader {

  override def load(): RDD[Order] = {
    val readConfig = ReadConfig.create(session).copy(collectionName = "orders")

    MongoSpark.load(session.sparkContext, readConfig)
      .filter(_.getString("status") == "COMPLETED")
      .map(DocumentToOrderConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Orders")
  }
}
