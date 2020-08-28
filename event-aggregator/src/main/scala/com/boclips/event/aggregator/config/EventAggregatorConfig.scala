package com.boclips.event.aggregator.config

case class EventAggregatorConfig(
                                  neo4jConfig: Option[Neo4jConfig] = None,
                                  youTubeConfig: Option[YouTubeConfig] = None
                                )
