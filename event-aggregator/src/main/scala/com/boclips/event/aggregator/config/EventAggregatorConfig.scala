package com.boclips.event.aggregator.config

case class EventAggregatorConfig(
                                  neo4j: Option[Neo4jConfig] = None,
                                  youTube: Option[YouTubeConfig] = None
                                )
