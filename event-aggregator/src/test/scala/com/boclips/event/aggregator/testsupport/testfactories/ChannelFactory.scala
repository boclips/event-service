package com.boclips.event.aggregator.testsupport.testfactories

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.presentation.formatters.schema.base.ExampleInstance

object ChannelFactory {
  def createChannel(
                     id: String = "channel-id",
                     name: String = "channel name",
                     details: ChannelDetails = ExampleInstance.create[ChannelDetails](),
                     ingest: ChannelIngest = ExampleInstance.create[ChannelIngest](),
                     pedagogy: ChannelPedagogy = ExampleInstance.create[ChannelPedagogy](),
                     marketing: ChannelMarketing = ExampleInstance.create[ChannelMarketing](),
                   ): Channel =
    Channel(
      id = ChannelId(id),
      name,
      details,
      ingest,
      pedagogy,
      marketing
    )
}
