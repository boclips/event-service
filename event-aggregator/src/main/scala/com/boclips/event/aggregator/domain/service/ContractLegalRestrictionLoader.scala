package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.ContractLegalRestriction
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

trait ContractLegalRestrictionLoader {
  def load()(implicit session: SparkSession): RDD[ContractLegalRestriction]
}
