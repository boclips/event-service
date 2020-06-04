package com.boclips.event.aggregator.config

import com.mongodb.ServerAddress

object MongoConfig {
  def apply(): MongoConfig = {
    MongoConfig(
      serverAddresses = Env("MONGO_HOSTS").split(",").map(new ServerAddress(_)).toList,
      replicaSetName = Some(Env("MONGO_REPLICA_SET")),
      database = Env("MONGO_DATABASE"),
      ssl = true,
      credentials = Some(MongoCredentials(
        authenticationDatabase = Env("MONGO_AUTHENTICATION_DATABASE"),
        username = Env("MONGO_USERNAME"),
        password = Env("MONGO_PASSWORD"),
      ))
    )
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
