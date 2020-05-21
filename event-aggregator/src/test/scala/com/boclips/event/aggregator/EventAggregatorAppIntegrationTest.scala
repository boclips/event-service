package com.boclips.event.aggregator

import com.boclips.event.aggregator.testsupport.{IntegrationTest, TestTableWriter}

class EventAggregatorAppIntegrationTest extends IntegrationTest {

  it should "calculate and write results with no error when there is no data" in mongoSparkTest { (spark, mongo) =>
    val app = new EventAggregatorApp(spark, new TestTableWriter())

    app.run()
  }

}
