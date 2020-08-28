package com.boclips.event.aggregator.config

case class YouTubeConfig(apiKey: String)

object YouTubeConfig {
  def fromEnv: YouTubeConfig =
    YouTubeConfig(apiKey = Env("YOUTUBE_API_KEY"))
}
