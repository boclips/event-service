package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.contentpartners.Contract
import com.boclips.event.aggregator.domain.service.ContractLoader
import com.boclips.event.infrastructure.contract.ContractDocument
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class MongoContractLoader(private val mongoClient: SparkMongoClient) extends ContractLoader {
  override def load()(implicit session: SparkSession): RDD[Contract] = {
    mongoClient
      .collectionRDD[ContractDocument]("contracts")
      .map(DocumentToContractConverter.convert)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Contracts")
  }
}
