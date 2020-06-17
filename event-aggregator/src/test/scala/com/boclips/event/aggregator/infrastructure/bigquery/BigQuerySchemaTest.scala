package com.boclips.event.aggregator.infrastructure.bigquery

import com.boclips.event.aggregator.presentation.VideoWithRelatedData
import com.boclips.event.aggregator.presentation.formatters.VideoFormatter
import com.boclips.event.aggregator.presentation.formatters.schema.Schema
import com.boclips.event.aggregator.presentation.formatters.schema.base.ExampleInstance
import com.boclips.event.aggregator.testsupport.Test

class BigQuerySchemaTest extends Test {

  it should "create a schema for videos" in {
    val video = ExampleInstance.create[VideoWithRelatedData]()
    val videoJson = VideoFormatter.formatRow(video)
    val videosSchema = Schema.fromJson(videoJson)
    val bigQuerySchema = BigQuerySchema.fieldsFrom(videosSchema)

    val channelField = bigQuerySchema.find(_.getName.equals("channel"))

    channelField should not be empty
    channelField.get.getType shouldBe "STRUCT"
    channelField.get.getMode shouldBe "NULLABLE"
    channelField.get.getFields should not be empty
  }

}
