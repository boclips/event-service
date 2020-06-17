package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.collections.Collection
import com.boclips.event.aggregator.domain.service.CollectionLoader
import com.boclips.event.infrastructure.collection.CollectionDocument
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoCollectionLoader(private val mongoClient: SparkMongoClient) extends CollectionLoader {

  override def load()(implicit session: SparkSession): RDD[Collection] = {
    mongoClient
      .collectionRDD[CollectionDocument]("collections")
      .map(DocumentToCollectionConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Collections")
  }

}
