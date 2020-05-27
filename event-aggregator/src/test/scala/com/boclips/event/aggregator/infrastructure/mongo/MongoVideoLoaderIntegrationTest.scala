package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createVideoDocument

class MongoVideoLoaderIntegrationTest extends IntegrationTest {
  "load" should "read videos" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo collection "videos"
    collection insertOne createVideoDocument()

    val videos = new MongoVideoLoader(mongo).load()(spark).collect()

    videos should have length 1
  }
}
