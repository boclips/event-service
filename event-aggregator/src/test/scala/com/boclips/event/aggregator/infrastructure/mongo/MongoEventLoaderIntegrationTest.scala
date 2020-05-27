package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideosSearchEventDocument
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createUser
import org.apache.spark.sql.SparkSession

class MongoEventLoaderIntegrationTest extends IntegrationTest {

  "load" should "read events" in mongoSparkTest { (spark: SparkSession, mongo) =>
    val collection = mongo collection "events"
    collection.insertOne(createVideosSearchEventDocument(userId = "dave@gmail.com"))
    collection.insertOne(createVideosSearchEventDocument(userId = "dave@gmail.com"))
    collection.insertOne(createVideosSearchEventDocument(userId = "john@boclips.com"))

    implicit val session: SparkSession = spark

    val boclipsEmployees = rdd(createUser(id = "john@boclips.com", isBoclipsEmployee = true))

    val events = new MongoEventLoader(mongo, boclipsEmployees).load.collect()

    events should have length 2
  }
}
