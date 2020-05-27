package com.boclips.event.aggregator.config

import com.mongodb.ServerAddress

object MongoConfig {
  def apply(): MongoConfig = {
    val serverAddresses = env("MONGO_HOST").split(",").map(new ServerAddress(_)).toList
    val replicaSetName = env("MONGO_REPLICA_SET")
    val databaseName = env("MONGO_INPUT_DATABASE")
    MongoConfig(
      serverAddresses = serverAddresses,
      replicaSetName = Some(replicaSetName),
      databaseName = databaseName,
      ssl = true,
    )
  }

  private def env(name: String): String = {
    val value = System.getenv(name)
    if (value == null) {
      throw new IllegalStateException(s"$name is not set")
    }
    value
  }
}

case class MongoConfig(
                        serverAddresses: List[ServerAddress],
                        replicaSetName: Option[String],
                        databaseName: String,
                        ssl: Boolean,
                      )
