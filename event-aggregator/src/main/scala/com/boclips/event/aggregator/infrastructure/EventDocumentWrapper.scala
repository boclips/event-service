package com.boclips.event.aggregator.infrastructure

import org.bson.Document

trait EventDocumentWrapper {

  val event: Document
  val externalUserExists: Boolean
}
