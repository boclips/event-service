package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.Video
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createVideoDocument
import org.apache.spark.sql.SparkSession

class MongoVideoLoaderIntegrationTest extends IntegrationTest {
  "load" should "read videos" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.db getCollection "videos"
    collection insertOne createVideoDocument()

    val videos = loadVideos(spark)

    videos should have length 1
  }

  private def loadVideos(session: SparkSession): Array[Video] =
    new MongoVideoLoader(session).load().collect()

}
