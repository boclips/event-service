package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.User
import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.service.EventLoader
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoEventLoader(
                        private val session: SparkSession,
                        private val boclipsEmployees: RDD[User]
                      ) extends EventLoader {

  override lazy val load: RDD[Event] = {

    val readConfig = ReadConfig.create(session).copy(collectionName = "events")

    val boclipsEmployeeIds = boclipsEmployees.map(_.id.value).collect().toSet

    MongoSpark.load(session.sparkContext, readConfig)
      .map(DocumentToEventConverter.convert)
      .filter(event => !boclipsEmployeeIds.contains(event.userId.value))
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Events")
  }
}
