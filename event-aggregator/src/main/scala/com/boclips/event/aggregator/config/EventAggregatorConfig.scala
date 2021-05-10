package com.boclips.event.aggregator.config

case class EventAggregatorConfig(
                                  youTube: Option[YouTubeConfig] = None,
                                  contentPackageMetrics: Option[ContentPackageMetricsConfig] = None,
                                )
