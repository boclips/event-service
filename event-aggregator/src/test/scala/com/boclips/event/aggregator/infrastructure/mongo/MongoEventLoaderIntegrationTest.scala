package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.users.{BoclipsUserIdentity, ExternalUserId, ExternalUserIdentity, UserId}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.{createVideoSegmentPlayedEventDocument, createVideosSearchEventDocument}
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createUser
import org.apache.spark.sql.SparkSession

class MongoEventLoaderIntegrationTest extends IntegrationTest {

  "load" should "read events" in mongoSparkTest { (spark: SparkSession, mongo) =>
    val collection = mongo collection "events"
    collection.insertOne(createVideosSearchEventDocument(userId = "dave@gmail.com"))
    collection.insertOne(createVideosSearchEventDocument(userId = "dave@gmail.com"))
    collection.insertOne(createVideosSearchEventDocument(userId = "john@boclips.com"))

    implicit val session: SparkSession = spark

    val boclipsEmployees = rdd(createUser(identity = UserFactory.createBoclipsUserIdentity("john@boclips.com"), isBoclipsEmployee = true))

    val events = new MongoEventLoader(mongo, rdd(),boclipsEmployees).load.collect()

    events should have length 2
  }

  "load" should "read events and override existing external user ids if they exist" in mongoSparkTest { (spark: SparkSession, mongo) =>
    val collection = mongo collection "events"
    collection.insertOne(createVideoSegmentPlayedEventDocument(userId = Some("lti-service-account"), externalUserId = Some("deployment-user")))
    collection.insertOne(createVideoSegmentPlayedEventDocument(userId = Some("pearson-service-account"), externalUserId = Some("non-existing-user")))


    implicit val session: SparkSession = spark

    val boclipsEmployees = rdd(createUser(identity = UserFactory.createBoclipsUserIdentity("john@boclips.com"), isBoclipsEmployee = true))

    val allUsers = rdd(
      createUser(identity = UserFactory.createBoclipsUserIdentity("lti-service-account")),
      createUser(identity = UserFactory.createBoclipsUserIdentity("deployment-user")),
      createUser(identity = UserFactory.createBoclipsUserIdentity("pearson-service-account")),
    )

    val events = new MongoEventLoader(mongo, allUsers, boclipsEmployees).load.collect()

    events should have length 2
    events(0).userIdentity.shouldBe(BoclipsUserIdentity(boclipsId = UserId("deployment-user")))
    events(1).userIdentity.shouldBe(ExternalUserIdentity(boclipsId = UserId("pearson-service-account"),externalId = ExternalUserId("non-existing-user")))
  }
}
