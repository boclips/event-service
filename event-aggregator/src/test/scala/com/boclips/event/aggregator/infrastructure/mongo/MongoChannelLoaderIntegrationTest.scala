package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.infrastructure.channel.ChannelDocument

class MongoChannelLoaderIntegrationTest extends IntegrationTest {
  "load" should "read channels" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.collection[ChannelDocument]("channels")
    collection insertOne ChannelDocument.sample.build()

    val channels = new MongoChannelLoader(mongo).load()(spark).collect()

    channels should have length 1
  }
}
