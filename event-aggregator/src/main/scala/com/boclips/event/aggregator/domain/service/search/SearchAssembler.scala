package com.boclips.event.aggregator.domain.service.search

import com.boclips.event.aggregator.domain.model.search.Search
import com.boclips.event.aggregator.domain.model.sessions.Session
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

class SearchAssembler(sessions: RDD[Session]) {

  def assembleSearches(): RDD[Search] = {
    val lessonPlanetId = "5f44da99-d18e-4b2e-a568-505005a0fa14"
    sessions
      .flatMap { sessionEvents =>
        new SessionSearchAssembler().assembleSearchesInSession(sessionEvents)
      }
      .persist(StorageLevel.MEMORY_AND_DISK)
      .filter(it => !it
        .request
        .userIdentity
        .id
        .map(_.value)
        .contains(lessonPlanetId) // remove all lesson planet searches.
      )
      .setName("Searches")
  }

}
