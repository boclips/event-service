package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Channel
import com.boclips.event.aggregator.domain.service.ChannelLoader
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoChannelLoader(private val session: SparkSession) extends ChannelLoader {
  override def load(): RDD[Channel] = {
    val readConfig = ReadConfig.create(session).copy(collectionName = "channels")

    MongoSpark.load(session.sparkContext, readConfig)
      .map(DocumentToChannelConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Channels")
  }
}
