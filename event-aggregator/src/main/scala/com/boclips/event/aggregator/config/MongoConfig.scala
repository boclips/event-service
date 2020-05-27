package com.boclips.event.aggregator.config

import com.mongodb.ServerAddress

object MongoConfig {
  def apply(): MongoConfig = {
    MongoConfig(
      serverAddresses = env("MONGO_HOSTS").split(",").map(new ServerAddress(_)).toList,
      replicaSetName = Some(env("MONGO_REPLICA_SET")),
      database = env("MONGO_DATABASE"),
      ssl = true,
      credentials = Some(MongoCredentials(
        authenticationDatabase = env("MONGO_AUTHENTICATION_DATABASE"),
        username = env("MONGO_USERNAME"),
        password = env("MONGO_PASSWORD"),
      ))
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
                        database: String,
                        ssl: Boolean,
                        credentials: Option[MongoCredentials],
                      )

case class MongoCredentials(
                        authenticationDatabase: String,
                        username: String,
                        password: String,
                           )
