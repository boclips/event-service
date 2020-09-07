package com.boclips.event.aggregator.config

import org.neo4j.driver.{AuthTokens, Driver, GraphDatabase}

case class Neo4jConfig(
                        username: String,
                        password: String,
                        boltUri: String
                      ) {
  def spawnDriver: Driver =
    GraphDatabase.driver(
      boltUri,
      AuthTokens.basic(
        username,
        password)
    )
}

object Neo4jConfig {
  def fromEnv: Option[Neo4jConfig] =
    try {
      Some(Neo4jConfig(
        username = Env("NEO4J_USERNAME"),
        password = Env("NEO4J_PASSWORD"),
        boltUri = Env("NEO4J_BOLT_URI")
      ))
    } catch {
      case _: IllegalStateException => None
    }
}
