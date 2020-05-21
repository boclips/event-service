package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Video
import com.boclips.event.aggregator.domain.service.VideoLoader
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoVideoLoader(private val session: SparkSession) extends VideoLoader {

  override def load(): RDD[Video] = {

    val readConfig = ReadConfig.create(session).copy(collectionName = "videos")

    MongoSpark.load(session.sparkContext, readConfig)
      .repartition(256)
      .map(DocumentToVideoConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Videos")
  }

}
