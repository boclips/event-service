package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory

class MongoChannelLoaderIntegrationTest extends IntegrationTest {
  "load" should "read channels" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo collection "channels"
    collection insertOne ChannelFactory.createChannelDocument()

    val channels = new MongoChannelLoader(mongo).load()(spark).collect()

    channels should have length 1
  }

}
