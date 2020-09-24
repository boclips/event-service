package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.infrastructure.ContractLegalRestrictionDocument

class MongoContractLegalRestrictionLoaderTest extends IntegrationTest {
  "load" should "read contract legal restrictions" in mongoSparkTest { (spark, mongo) =>
    val collection = mongo.collection[ContractLegalRestrictionDocument]("contract-legal-restrictions")
    collection insertOne ContractLegalRestrictionDocument.sample.build()

    val contractLegalRestriction = new MongoContractLegalRestrictionLoader(mongo).load()(spark).collect()

    contractLegalRestriction should have length 1
  }
}

