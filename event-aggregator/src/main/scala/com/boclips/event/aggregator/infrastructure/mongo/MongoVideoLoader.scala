package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.videos.Video
import com.boclips.event.aggregator.domain.service.VideoLoader
import com.boclips.event.infrastructure.video.VideoDocument
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoVideoLoader(private val mongoClient: SparkMongoClient) extends VideoLoader {

  override def load()(implicit session: SparkSession): RDD[Video] = {
    mongoClient
      .collectionRDD[VideoDocument]("videos")
      .repartition(256)
      .map(DocumentToVideoConverter.convert)
      .persist(StorageLevel.DISK_ONLY)
      .setName("Videos")
  }

}
