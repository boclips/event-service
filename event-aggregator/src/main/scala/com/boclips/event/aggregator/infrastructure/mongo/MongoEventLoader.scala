package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.User
import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.service.EventLoader
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoEventLoader(
                        private val mongoClient: SparkMongoClient,
                        private val boclipsEmployees: RDD[User],
                      ) extends EventLoader {

  override def load()(implicit session: SparkSession): RDD[Event] = {
    val boclipsEmployeeIds = boclipsEmployees
      .map(_.identity).collect().toSet

    mongoClient
      .collectionRDD("events")
      .map(DocumentToEventConverter.convert)
      .filter(event => event.userIdentity.id.isEmpty || !boclipsEmployeeIds.contains(event.userIdentity))
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Events")
  }
}
