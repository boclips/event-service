package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.infrastructure.video.VideoDocument

class MongoVideoLoaderIntegrationTest extends IntegrationTest {
  "load" should "read videos" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.collection[VideoDocument]("videos")
    collection insertOne VideoDocument.sample().id("video-id").build()

    val videos = new MongoVideoLoader(mongo).load()(spark).collect()

    videos should have length 1
    videos.head.id shouldBe VideoId("video-id")
  }
}
