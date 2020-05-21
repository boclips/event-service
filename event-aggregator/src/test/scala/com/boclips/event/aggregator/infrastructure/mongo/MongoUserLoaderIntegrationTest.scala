package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.{User, UserId}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createUserDocument
import org.apache.spark.sql.SparkSession

class MongoUserLoaderIntegrationTest extends IntegrationTest {

  "loadAllUsers" should "read non boclips users" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.db getCollection "users"
    collection insertOne createUserDocument()

    val users = loadUsers(spark)

    users should have length 1
  }

  it should "not ignore boclips employees" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.db getCollection "users"
    collection insertOne createUserDocument(isBoclipsEmployee = true)

    val users = loadUsers(spark)

    users should have length 1
  }

  "loadBoclipsEmployees" should "read boclips users" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.db getCollection "users"
    collection insertOne createUserDocument(id = "1", isBoclipsEmployee = true)
    collection insertOne createUserDocument(id = "2", boclipsEmployee = true)

    val users = loadBoclipsUsers(spark)

    users should have length 2
    users.map(_.id) shouldBe List(UserId("1"), UserId("2"))
  }

  it should "ignore non-boclips users" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.db getCollection "users"
    collection insertOne createUserDocument(id = "3", isBoclipsEmployee = false)
    collection insertOne createUserDocument(id = "4", boclipsEmployee = false)

    val users = loadBoclipsUsers(spark)

    users should have length 0
  }

  private def loadUsers(session: SparkSession): Array[User] = new MongoUserLoader(session).loadAllUsers.collect()

  private def loadBoclipsUsers(session: SparkSession): Array[User] = new MongoUserLoader(session).loadBoclipsEmployees.collect()

}
