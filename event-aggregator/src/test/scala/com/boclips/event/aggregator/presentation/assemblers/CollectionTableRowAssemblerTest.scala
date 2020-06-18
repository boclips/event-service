package com.boclips.event.aggregator.presentation.assemblers

import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.CollectionFactory.createCollection
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createCollectionInteractedWithEvent
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createCollectionSearchResultImpression, createSearchRequest}

class CollectionTableRowAssemblerTest extends IntegrationTest {

  it should "relevant collection items" in sparkTest { implicit spark =>

    val collections = rdd(
      createCollection(id = "c1"),
      createCollection(id = "c2"),
    )

    val impressions = rdd(
      createCollectionSearchResultImpression(collectionId = CollectionId("c1"), search = createSearchRequest(query = "math"), interaction = true),
      createCollectionSearchResultImpression(collectionId = CollectionId("c1"), search = createSearchRequest(query = "coronavirus"), interaction = false)
    )

    val interactions = rdd(
      createCollectionInteractedWithEvent(collectionId = "c1", subtype = Some("VISIT_LESSON_GUIDE")),
      createCollectionInteractedWithEvent(collectionId = "c1", subtype = Some("NAVIGATE_TO_COLLECTION_DETAILS")),
    )

    val collectionsWithRelatedData = CollectionTableRowAssembler.assembleCollectionsWithRelatedData(
      collections = collections,
      impressions = impressions,
      interactions = interactions).collect().toList.sortBy(_.collection.id.value)


    collectionsWithRelatedData should have size 2
    collectionsWithRelatedData.head.collection.id shouldBe CollectionId("c1")
    collectionsWithRelatedData.head.impressions should have size 2

    val c1Impressions = collectionsWithRelatedData.head.impressions.sortBy(_.search.query.value)

    c1Impressions.head.interaction shouldBe false

  }

  it should "aggregate collection interactions" in sparkTest { implicit spark =>

    val collections = rdd(
      createCollection(id = "c1"),
      createCollection(id = "c2"),
    )

    val impressions = rdd(
      createCollectionSearchResultImpression(collectionId = CollectionId("c1"), search = createSearchRequest(query = "math"), interaction = true),
      createCollectionSearchResultImpression(collectionId = CollectionId("c1"), search = createSearchRequest(query = "coronavirus"), interaction = false)
    )

    val interactions = rdd(
      createCollectionInteractedWithEvent(collectionId = "c1", subtype = Some("VISIT_LESSON_GUIDE")),
      createCollectionInteractedWithEvent(collectionId = "c1", subtype = Some("NAVIGATE_TO_COLLECTION_DETAILS")),
    )

    val collectionsWithRelatedData = CollectionTableRowAssembler.assembleCollectionsWithRelatedData(
      collections = collections,
      impressions = impressions,
      interactions = interactions).collect().toList.sortBy(_.collection.id.value)


    collectionsWithRelatedData should have size 2
    collectionsWithRelatedData.head.collection.id shouldBe CollectionId("c1")
    collectionsWithRelatedData.head.impressions should have size 2

  }

}
