package com.boclips.event.aggregator.domain.service.collection

import com.boclips.event.aggregator.domain.model.search.{CollectionSearchResultImpression, Search}
import org.apache.spark.rdd.RDD

object CollectionSearchResultImpressionAssembler {

  def apply(searches: RDD[Search]): RDD[CollectionSearchResultImpression] = {
    searches.flatMap(search =>
      search.response.collectionResults.map(result =>
        CollectionSearchResultImpression(collectionId = result.collectionId,
          search = search.request,
          interaction = result.interaction
        )
      )
    )
  }
}
