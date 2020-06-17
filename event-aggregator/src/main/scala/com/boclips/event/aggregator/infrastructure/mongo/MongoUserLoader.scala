package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.users.User
import com.boclips.event.aggregator.domain.service.UserLoader
import com.boclips.event.infrastructure.user.UserDocument
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
      .collectionRDD[UserDocument]("users")
      .map(DocumentToUserConverter.convert)
      .repartition(10)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Users")
  }
}
