package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Order
import com.boclips.event.aggregator.domain.service.OrderLoader
import com.mongodb.{MongoClient, MongoClientOptions, MongoClientURI, ServerAddress}
import com.mongodb.spark.config.ReadConfig
import com.mongodb.spark.{MongoClientFactory, MongoConnector, MongoSpark}
import com.order.OrderDocument
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.bson.codecs.configuration.CodecRegistry

class MongoOrderLoader(private val session: SparkSession, mongoSpark: MongoSpark.Builder) extends OrderLoader {

  override def load(): RDD[Order] = {
    val readConfig = ReadConfig
      .create(session)
      .copy(collectionName = "orders")

    mongoSpark
      .readConfig(readConfig)
      .build()
      .toRDD[OrderDocument]()
      .filter(_.getStatus == "COMPLETED")
      .map(DocumentToOrderConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Orders")
  }
}