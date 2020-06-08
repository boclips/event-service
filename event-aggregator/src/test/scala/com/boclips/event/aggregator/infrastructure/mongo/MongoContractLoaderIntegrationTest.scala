package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.infrastructure.contract.ContractDocument

class MongoContractLoaderIntegrationTest extends IntegrationTest {
  "load" should "read channels" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.collection[ContractDocument]("contracts")
    collection insertOne ContractDocument.sample.build()

    val contracts = new MongoContractLoader(mongo).load()(spark).collect()

    contracts should have length 1
  }
}
