package com.boclips.event.aggregator.domain.service.collection

import com.boclips.event.aggregator.domain.model.events.CollectionInteractedWithEvent
import com.boclips.event.aggregator.domain.model.{Collection, CollectionId, CollectionSearchResultImpression, CollectionWithRelatedData}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

object CollectionAssembler {

  def assembleCollectionsWithRelatedData(collections: RDD[Collection],
                                         impressions: RDD[CollectionSearchResultImpression],
                                         interactions: RDD[CollectionInteractedWithEvent],
                                        ) : RDD[CollectionWithRelatedData] = {

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
      .map {case ((collection, impressions), interactions) => CollectionWithRelatedData(
        collection = collection,
        impressions = impressions,
        interactions = interactions
      )
      }
      .setName("Collections with related data")
      .persist(StorageLevel.DISK_ONLY)

  }


}
