package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Video
import com.boclips.event.aggregator.domain.service.VideoLoader
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoVideoLoader(private val mongoClient: SparkMongoClient) extends VideoLoader {

  override def load()(implicit session: SparkSession): RDD[Video] = {
    mongoClient
      .collectionRDD("videos")
      .repartition(256)
      .map(DocumentToVideoConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Videos")
  }

}
