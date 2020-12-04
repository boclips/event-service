package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.contentpackages.ContentPackage
import com.boclips.event.aggregator.domain.service.ContentPackageLoader
import com.boclips.event.infrastructure.collection.CollectionDocument
import com.boclips.event.infrastructure.contentpackage.ContentPackageDocument
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoContentPackageLoader(private val mongoClient: SparkMongoClient) extends ContentPackageLoader {
  override def load()(implicit session: SparkSession): RDD[ContentPackage] = {
    mongoClient
      .collectionRDD[ContentPackageDocument]("content-packages")
      .map(DocumentToContentPackageConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Content Packages")
  }
}
