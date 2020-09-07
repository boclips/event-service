package com.boclips.event.aggregator.config

case class EventAggregatorConfig(
                                  neo4jEnabled: Boolean = false,
                                  youTubeConfig: Option[YouTubeConfig] = None
                                )
