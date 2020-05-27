package com.boclips.event.aggregator.testsupport

import com.boclips.event.aggregator.domain.model.User
import com.boclips.event.aggregator.domain.service.UserLoader
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class TestUserLoader(private val users: Seq[User])(implicit spark: SparkSession) extends UserLoader {

  override def loadAllUsers()(implicit session: SparkSession): RDD[User] = spark.sparkContext.parallelize(users)

  override def loadBoclipsEmployees()(implicit session: SparkSession): RDD[User] = loadAllUsers()(session).filter(_.isBoclipsEmployee)
}
