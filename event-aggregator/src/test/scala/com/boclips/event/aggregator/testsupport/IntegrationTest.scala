package com.boclips.event.aggregator.testsupport

import com.boclips.event.aggregator.config.{MongoConfig, SparkConfig}
import com.boclips.event.aggregator.infrastructure.mongo.SparkMongoClient
import com.mongodb.internal.connection.ServerAddressHelper
import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodStarter}
import de.flapdoodle.embed.process.runtime.Network
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.reflect.ClassTag

trait IntegrationTest extends Test {

  def mongoSparkTest(testMethod: (SparkSession, SparkMongoClient) => Any): Unit = {
    val starter = MongodStarter.getDefaultInstance

    val bindIp = "localhost"
    val port = Network.getFreeServerPort
    val mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION).net(new Net(bindIp, port, Network.localhostIsIPv6)).build
    var mongodExecutable: MongodExecutable = null

    try {
      mongodExecutable = starter.prepare(mongodConfig)
      mongodExecutable.start

      val serverAddress = ServerAddressHelper.createServerAddress(bindIp, port)
      val mongo = new SparkMongoClient(MongoConfig(
        serverAddresses = serverAddress :: Nil,
        replicaSetName = None,
        database = "test-database",
        ssl = false,
        credentials = None,
      ))

      runTest[Some[SparkMongoClient]]((session, mongo) => testMethod(session, mongo.get), Some(mongo))
    } finally {
      if (mongodExecutable != null) mongodExecutable.stop()
    }
  }

  def runTest[TMongoOption <: Option[SparkMongoClient]](testMethod: (SparkSession, TMongoOption) => Any, mongo: TMongoOption): Unit = {

    var sparkSession: SparkSession = null

    try {
      sparkSession = new SparkConfig(None, 4).session

      testMethod(sparkSession, mongo)
    } finally {
      if (sparkSession != null) sparkSession.stop()
    }
  }

  def sparkTest(testMethod: SparkSession => Any): Unit = {
    runTest[Option[SparkMongoClient]]((session, _) => testMethod(session), None)
  }

  def rdd[T: ClassTag](items: T*)(implicit session: SparkSession): RDD[T] = session.sparkContext.parallelize(items)

  def emptyRdd()(implicit session: SparkSession): RDD[Nothing] = rdd[Nothing]()
}
