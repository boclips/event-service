package com.boclips.event.aggregator.infrastructure.neo4j

import com.boclips.event.aggregator.presentation.model.VideoTableRow
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory.createChannel
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createVideo
import com.dimafeng.testcontainers.{ForAllTestContainer, Neo4jContainer}
import org.neo4j.driver.{AuthTokens, Driver, GraphDatabase, Record, Result}

class VideoRowGraphWriterIntegrationTest extends Test with ForAllTestContainer {
  override val container: Neo4jContainer =
    Neo4jContainer(neo4jImageVersion = "neo4j:4.0.6")

  it should "populate neo4j with a connected video and channel" in {
    withDriver(driver => {
      val writer = new Neo4jVideoRowGraphWriter(driver)
      val video = createVideo(
        id = "this-video-id",
        title = "my video"
      )
      val channel = createChannel(
        id = "this-channel-id",
        name = "my channel"
      )
      val row = VideoTableRow(
        video = video,
        channel = Some(channel)
      )
      writer.write(List(row))

      val session = driver.session

      var record: Record = null;
      try {
        record = session.readTransaction(tx => {
          val result = tx.run(
            """
              |MATCH
              |  (video:Video)-->(channel:Channel)
              |RETURN
              |  video, channel
              |""".stripMargin
          )
          result.single
        })
      } finally if (session != null) session.close()

      val videoResult = record.get("video")
      val channelResult = record.get("channel")

      videoResult.get("uuid").asString shouldBe video.id.value
      videoResult.get("title").asString shouldBe video.title
      channelResult.get("uuid").asString shouldBe channel.id.value
      channelResult.get("name").asString shouldBe channel.name
    })
  }

  private def withDriver(fn: Driver => Unit): Unit = {
    val driver = GraphDatabase.driver(
      container.boltUrl,
      AuthTokens.basic(
        container.username,
        container.password
      )
    )
    try {
      fn(driver)
    } finally driver.close()
  }
}
