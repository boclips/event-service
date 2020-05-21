package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Channel
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory
import org.apache.spark.sql.SparkSession

class MongoChannelLoaderIntegrationTest extends IntegrationTest {
  "load" should "read channels" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.db getCollection "channels"
    collection insertOne ChannelFactory.createChannelDocument()

    val channels = loadChannels(spark)

    channels should have length 1
  }

  private def loadChannels(session: SparkSession): Array[Channel] =
    new MongoChannelLoader(session).load().collect()
}
