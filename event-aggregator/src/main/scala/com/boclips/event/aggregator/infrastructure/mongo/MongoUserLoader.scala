package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.User
import com.boclips.event.aggregator.domain.service.UserLoader
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoUserLoader(private val mongoClient: SparkMongoClient) extends UserLoader {

  override def loadAllUsers()(implicit session: SparkSession): RDD[User] = all

  override def loadBoclipsEmployees()(implicit session: SparkSession): RDD[User] = {
    all.filter(_.isBoclipsEmployee)
  }

  private def all()(implicit session: SparkSession): RDD[User] = {
    mongoClient
      .collectionRDD("users")
      .map(DocumentToUserConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Users")
  }
}
