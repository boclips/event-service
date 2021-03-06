package com.boclips.event.aggregator.presentation.assemblers

import com.boclips.event.aggregator.domain.model.collections.{Collection, CollectionId}
import com.boclips.event.aggregator.domain.model.events.CollectionInteractedWithEvent
import com.boclips.event.aggregator.domain.model.search.CollectionSearchResultImpression
import com.boclips.event.aggregator.presentation.model
import com.boclips.event.aggregator.presentation.model.CollectionTableRow
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

object CollectionTableRowAssembler {

  def assembleCollectionsWithRelatedData(collections: RDD[Collection],
                                         impressions: RDD[CollectionSearchResultImpression],
                                         interactions: RDD[CollectionInteractedWithEvent],
                                        ): RDD[CollectionTableRow] = {

    val impressionsByCollectionId: RDD[(CollectionId, Iterable[CollectionSearchResultImpression])] = impressions
      .keyBy(_.collectionId)
      .groupByKey()

    val interactionsByCollectionId: RDD[(CollectionId, Iterable[CollectionInteractedWithEvent])] = interactions
      .keyBy(_.collectionId)
      .groupByKey()

    collections.keyBy(_.id)
      .leftOuterJoin(impressionsByCollectionId)
      .leftOuterJoin(interactionsByCollectionId)
      .values
      .map { case ((collection, impressions), interactions) => model.CollectionTableRow(
        collection = collection,
        impressions = impressions,
        interactions = interactions
      )
      }
      .setName("Collections with related data")
      .persist(StorageLevel.DISK_ONLY)

  }


}
