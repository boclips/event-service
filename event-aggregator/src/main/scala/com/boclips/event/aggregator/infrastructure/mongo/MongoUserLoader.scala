package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.User
import com.boclips.event.aggregator.domain.service.UserLoader
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoUserLoader(private val session: SparkSession) extends UserLoader {

  override lazy val loadAllUsers: RDD[User] = all

  override lazy val loadBoclipsEmployees: RDD[User] = {
    all.filter(_.isBoclipsEmployee)
  }

  private lazy val all: RDD[User] = {
    val readConfig = ReadConfig.create(session).copy(collectionName = "users")
    MongoSpark.load(session.sparkContext, readConfig)
      .map(DocumentToUserConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Users")
  }
}
