package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Channel
import com.boclips.event.aggregator.domain.service.ChannelLoader
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoChannelLoader(private val mongoClient: SparkMongoClient) extends ChannelLoader {
  override def load()(implicit session: SparkSession): RDD[Channel] = {
    mongoClient
      .collectionRDD("channels")
      .map(DocumentToChannelConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Channels")
  }
}
