package com.boclips.event.aggregator.infrastructure.mongo

case class MongoConnectionDetails(hosts: String, databaseName: String, options: Option[String] = None) {
  def getConnectionUri() = s"mongodb://$hosts/$databaseName.collection${options.map(opts => s"?$opts").getOrElse("")}"
}
