package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.contentpartners.Channel
import com.boclips.event.aggregator.domain.service.ChannelLoader
import com.boclips.event.aggregator.infrastructure.mongo.MongoChannelLoader.CHANNELS_COLLECTION
import com.boclips.event.infrastructure.channel.ChannelDocument
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

object MongoChannelLoader {
  final val CHANNELS_COLLECTION = "channels"

}

class MongoChannelLoader(private val mongoClient: SparkMongoClient) extends ChannelLoader {
  override def load()(implicit session: SparkSession): RDD[Channel] = {
    mongoClient
      .collectionRDD[ChannelDocument](CHANNELS_COLLECTION)
      .map(DocumentToChannelConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Channels")
  }
}
