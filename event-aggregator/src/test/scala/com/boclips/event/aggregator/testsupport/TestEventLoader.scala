package com.boclips.event.aggregator.testsupport

import com.boclips.event.aggregator.domain.model.events.Event
import com.boclips.event.aggregator.domain.service.EventLoader
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class TestEventLoader(private val events: Seq[Event])(implicit spark: SparkSession) extends EventLoader {

  override def load(): RDD[Event] = {
    spark.sparkContext.parallelize(events)
  }
}
