package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.User
import org.apache.spark.rdd.RDD

trait UserLoader {

  def loadAllUsers(): RDD[User]

  def loadBoclipsEmployees(): RDD[User]
}
