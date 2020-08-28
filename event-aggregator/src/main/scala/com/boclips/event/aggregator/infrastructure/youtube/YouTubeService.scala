package com.boclips.event.aggregator.infrastructure.youtube

import com.boclips.event.aggregator.config.YouTubeConfig
import com.boclips.event.aggregator.domain.model.videos.{VideoId, YouTubeVideoStats}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequest
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube

import scala.collection.JavaConverters._

case class YouTubeService(config: YouTubeConfig) {
  val client: YouTube = getClient

  private def getClient: YouTube = {
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport
    new YouTube.Builder(
      httpTransport,
      JacksonFactory.getDefaultInstance,
      (_: HttpRequest) => {}
    ).setApplicationName("event-aggregator").build()
  }

  def getVideoStats(
                     videoIdsByPlaybackId: Map[String, VideoId]
                   ): List[YouTubeVideoStats] = {
    if (videoIdsByPlaybackId.isEmpty) return List()
    videoIdsByPlaybackId.keys.grouped(50).flatMap { group =>
      val channelRequest = client
        .videos()
        .list(List("id", "statistics").asJava)
        .setId(group.toList.asJava)
        .setKey(config.apiKey)
        .setMaxResults(50L)
      channelRequest
        .execute
        .getItems.iterator().asScala.toList.flatMap(item => {
        val stats = item.getStatistics
        try {
          Some(YouTubeVideoStats(
            videoId = videoIdsByPlaybackId(item.getId),
            viewCount = stats.getViewCount.longValue()
          ))
        } catch {
          case _: NullPointerException =>
            println(s"No stats found for video with YouTube ID ${item.getId}")
            None
        }
      })
    }.toList
  }
}

