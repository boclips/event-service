package com.boclips.event.aggregator.config

case class ContentPackageMetricsConfig(
                                        videoServiceUri: String,
                                        tokenUrl: String,
                                        clientId: String,
                                        clientSecret: String
                                      )

object ContentPackageMetricsConfig {
  def fromEnv: ContentPackageMetricsConfig =
    ContentPackageMetricsConfig(
      videoServiceUri = System.getenv("VIDEO_SERVICE_URI"),
      tokenUrl = System.getenv("TOKEN_URL"),
      clientId = System.getenv("CLIENT_ID"),
      clientSecret = System.getenv("OAUTH2_CLIENT_SECRET")
    )
}
