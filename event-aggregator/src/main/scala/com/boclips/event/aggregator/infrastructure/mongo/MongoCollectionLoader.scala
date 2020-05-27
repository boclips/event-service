package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Collection
import com.boclips.event.aggregator.domain.service.CollectionLoader
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoCollectionLoader(private val mongoClient: SparkMongoClient) extends CollectionLoader {

  override def load()(implicit session: SparkSession): RDD[Collection] = {
    mongoClient
      .collectionRDD("collections")
      .map(DocumentToCollectionConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Collections")
  }

}
