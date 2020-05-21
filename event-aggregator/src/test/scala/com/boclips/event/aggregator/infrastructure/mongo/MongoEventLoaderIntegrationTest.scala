package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.User
import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideosSearchEventDocument
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createUser
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class MongoEventLoaderIntegrationTest extends IntegrationTest {

  "load" should "read events" in mongoSparkTest { (spark: SparkSession, mongo) =>
    val collection = mongo.db getCollection "events"
    collection.insertOne(createVideosSearchEventDocument(userId = "dave@gmail.com"))
    collection.insertOne(createVideosSearchEventDocument(userId = "dave@gmail.com"))
    collection.insertOne(createVideosSearchEventDocument(userId = "john@boclips.com"))

    implicit val session: SparkSession = spark

    val boclipsEmployees = rdd(createUser(id = "john@boclips.com", isBoclipsEmployee = true))

    val events = loadEvents(spark, boclipsEmployees)

    events should have length 2
  }

  private def loadEvents(session: SparkSession, boclipsEmployees: RDD[User]): Array[Event] = new MongoEventLoader(session, boclipsEmployees).load.collect()
}
