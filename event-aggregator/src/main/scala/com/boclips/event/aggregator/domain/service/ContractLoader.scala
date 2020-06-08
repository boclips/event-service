package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.Contract
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

trait ContractLoader {
    def load()(implicit session: SparkSession): RDD[Contract]
}
