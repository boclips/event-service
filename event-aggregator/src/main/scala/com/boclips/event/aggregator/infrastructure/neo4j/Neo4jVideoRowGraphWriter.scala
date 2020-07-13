package com.boclips.event.aggregator.infrastructure.neo4j

import com.boclips.event.aggregator.presentation.model.VideoTableRow
import org.neo4j.driver.Values.parameters
import org.neo4j.driver.{Driver, Transaction}

import scala.collection.JavaConverters._

class Neo4jVideoRowGraphWriter(val driver: Driver) {
  def write(videoRow: List[VideoTableRow]): Unit = {
    val session = driver.session
    try {
      session.writeTransaction((tx: Transaction) => {
        val videoParams = videoRow.map(it =>
          Map(
            "id" -> it.video.id.value,
            "title" -> it.video.title
          ).asJava
        ).asJava

        tx.run(
          """
            |UNWIND
            |  $videos as v
            |MERGE
            |  (video:Video {uuid: v.id})
            |SET
            |  video.title = v.title
            |""".stripMargin,
          parameters(
            "videos", videoParams
          )
        )

        val channelParams = videoRow.flatMap(it =>
          it.channel.map(channel =>
            Map(
              "id" -> channel.id.value,
              "name" -> channel.name,
              "videoId" -> it.video.id.value
            ).asJava
          )
        ).asJava

        tx.run(
          """UNWIND
            |  $channels as c
            |MATCH
            |  (video:Video {uuid: c.videoId})
            |MERGE
            |  (channel:Channel {uuid: c.id})
            |MERGE
            |  (video)-[:BELONGS_TO_CHANNEL]->(channel)
            |SET
            |  channel.name = c.name
            |""".stripMargin,
          parameters(
            "channels", channelParams
          )
        )
      })
    } finally if (session != null) session.close()
  }

  def cleanUp(): Unit = driver.close()
}
