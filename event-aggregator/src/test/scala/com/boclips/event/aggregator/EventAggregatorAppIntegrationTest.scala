package com.boclips.event.aggregator

import com.boclips.event.aggregator.config.EventAggregatorConfig
import com.boclips.event.aggregator.infrastructure.mongo.MongoEventLoader.EVENTS_COLLECTION
import com.boclips.event.aggregator.infrastructure.mongo.MongoVideoLoader.VIDEOS_COLLECTION
import com.boclips.event.aggregator.presentation.TableNames
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoSegmentPlayedEventDocument
import com.boclips.event.aggregator.testsupport.{IntegrationTest, TestTableWriter}
import com.boclips.event.infrastructure.video.VideoDocument

class EventAggregatorAppIntegrationTest extends IntegrationTest {

  it should "calculate and write results with no error when there is no data" in mongoSparkTest { (spark, mongo) =>
    val app = new EventAggregatorApp(
      new TestTableWriter(),
      mongo
    )(spark)

    app.run()
  }

  it should "write videos with playback data" in mongoSparkTest { (spark, mongo) =>
    val videosCollection = mongo.collection[VideoDocument](VIDEOS_COLLECTION)
    videosCollection.insertOne(
      VideoDocument.sample().id("v1").build()
    )

    val eventsCollection = mongo.collection(EVENTS_COLLECTION)
    eventsCollection.insertOne(
      createVideoSegmentPlayedEventDocument(userId = None, deviceId = Some("device-1"), videoId = "v1")
    )
    eventsCollection.insertOne(
      createVideoSegmentPlayedEventDocument(userId = None, deviceId = Some("device-2"), videoId = "v1")
    )

    val tableWriter = new TestTableWriter()
    val app = new EventAggregatorApp(
      tableWriter,
      mongo
    )(spark)

    app.run()

    val videosTable = tableWriter.table(TableNames.VIDEOS)
    videosTable should not be empty
    videosTable.get should have size 1
    videosTable.get.head.getAsJsonArray("playbacks").size() shouldBe 2
    List("device-1", "device-2") should contain(
      videosTable.get.head.getAsJsonArray("playbacks").get(0).getAsJsonObject.get("deviceId").getAsString
    )
  }
}
