package com.boclips.event.aggregator.domain.service.storage

import java.time.LocalDate

import com.boclips.event.aggregator.domain.model.{ContentPartnerStorageCharge, Video, VideoStorageCharge}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

class StorageChargesAssembler(videos: RDD[Video]) {

  lazy val assembleStorageCharges: RDD[VideoStorageCharge] = {
    videos
      .flatMap(_.storageCharges(to = LocalDate.now()))
      .repartition(128)
      .persist(StorageLevel.DISK_ONLY)
      .setName("Storage charges")
  }
}
