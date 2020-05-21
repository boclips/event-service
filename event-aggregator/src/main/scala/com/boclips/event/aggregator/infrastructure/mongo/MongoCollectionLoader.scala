package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Collection
import com.boclips.event.aggregator.domain.service.CollectionLoader
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoCollectionLoader(private val session: SparkSession) extends CollectionLoader {

  override def load(): RDD[Collection] = {

    val readConfig = ReadConfig.create(session).copy(collectionName = "collections")

    MongoSpark.load(session.sparkContext, readConfig)
      .map(DocumentToCollectionConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Collections")
  }

}
