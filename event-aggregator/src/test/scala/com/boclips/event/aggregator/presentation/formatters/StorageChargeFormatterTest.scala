package com.boclips.event.aggregator.presentation.formatters

import java.time.LocalDate

import com.boclips.event.aggregator.domain.model.VideoId
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createStorageCharge

class StorageChargeFormatterTest extends Test {

  it should "write fields to json" in {
    val storageCharge = createStorageCharge(
      videoId = VideoId("v1"),
      periodEnd = LocalDate.parse("2020-03-20"),
      valueGbp = 0.3,
    )

    val json = StorageChargeFormatter.formatRow(storageCharge)

    json.getString("id") shouldBe "v1-2020-03-20"
    json.getDouble("valueGbp") shouldBe 0.3
  }
}
