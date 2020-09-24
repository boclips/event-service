package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.ContractLegalRestriction
import com.boclips.event.aggregator.domain.service.ContractLegalRestrictionLoader
import com.boclips.event.infrastructure.ContractLegalRestrictionDocument
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoContractLegalRestrictionLoader(private val mongoClient: SparkMongoClient) extends ContractLegalRestrictionLoader {
  override def load()(implicit session: SparkSession): RDD[ContractLegalRestriction] = {
    mongoClient
      .collectionRDD[ContractLegalRestrictionDocument]("contract-legal-restrictions")
      .map(DocumentToContractLegalRestrictionConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Contracts Legal Restrictions")
  }
}

