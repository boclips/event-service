package com.boclips.event.aggregator.domain.service.search

import com.boclips.event.aggregator.domain.model.{Search, Session}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

class SearchAssembler(sessions: RDD[Session]) {

  def assembleSearches(): RDD[Search] = {
    sessions
      .flatMap { sessionEvents => new SessionSearchAssembler().assembleSearchesInSession(sessionEvents) }
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Searches")
  }

}
