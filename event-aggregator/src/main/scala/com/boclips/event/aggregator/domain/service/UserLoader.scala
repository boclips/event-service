package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.users.User
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

trait UserLoader {

  def loadAllUsers()(implicit session: SparkSession): RDD[User]

  def loadBoclipsEmployees()(implicit session: SparkSession): RDD[User]
}
