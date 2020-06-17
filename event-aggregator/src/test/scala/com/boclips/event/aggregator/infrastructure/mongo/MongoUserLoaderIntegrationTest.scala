package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.{BoclipsUserIdentity, User, UserId}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.infrastructure.user.UserDocument
import com.mongodb.client.MongoCollection
import org.apache.spark.sql.SparkSession

class MongoUserLoaderIntegrationTest extends IntegrationTest {

  "loadAllUsers" should "read users" in mongoSparkTest { (spark, mongo) =>
    val collection = getCollection(mongo)
    collection insertOne UserDocument.sample.id("my-id").build()

    val rawCollection = mongo.collection("users")
    val rawDocument = rawCollection.find().iterator().next()
    rawDocument.get("_id") shouldBe "my-id"

    val document = collection.find().iterator().next;
    document.getId shouldBe "my-id"

    val users = loadUsers(spark, mongo)

    users should have length 1
    users.head.identity shouldBe BoclipsUserIdentity(UserId("my-id"))
  }

  it should "not ignore boclips employees" in mongoSparkTest { (spark, mongo) =>
    val collection = getCollection(mongo)
    collection insertOne UserDocument.sample
      .boclipsEmployee(true)
      .build()

    val users = loadUsers(spark, mongo)

    users should have length 1
  }

  "loadBoclipsEmployees" should "read boclips users" in mongoSparkTest { (spark, mongo) =>
    val collection = getCollection(mongo)
    val sample1 = UserDocument.sample
      .id("1")
      .boclipsEmployee(true)
      .build()
    val sample2 = UserDocument.sample
      .id("2")
      .boclipsEmployee(true)
      .build()
    collection insertOne sample1
    collection insertOne sample2

    val users = loadBoclipsUsers(spark, mongo)

    users should have length 2
    users.map(_.identity) shouldBe List(BoclipsUserIdentity(UserId("1")), BoclipsUserIdentity(UserId("2")))
  }

  it should "ignore non-boclips users" in mongoSparkTest { (spark, mongo) =>
    val collection = getCollection(mongo)
    collection insertOne UserDocument.sample
      .id("3")
      .boclipsEmployee(false)
      .build()
    collection insertOne UserDocument.sample
      .id("4")
      .boclipsEmployee(false)
      .build()

    val users = loadBoclipsUsers(spark, mongo)

    users should have length 0
  }

  private def getCollection(mongo: SparkMongoClient): MongoCollection[UserDocument] =
    mongo.collection[UserDocument]("users")

  private def loadUsers(implicit session: SparkSession, mongo: SparkMongoClient): Array[User] =
    new MongoUserLoader(mongo).loadAllUsers().collect()

  private def loadBoclipsUsers(implicit session: SparkSession, mongo: SparkMongoClient): Array[User] =
    new MongoUserLoader(mongo).loadBoclipsEmployees().collect()

}
