package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.videos.VideoTopic
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object VideoTopicThreeLevelFormatter extends SingleRowFormatter[VideoTopic] {
  override def writeRow(topic: VideoTopic, json: JsonObject): Unit = {
    val createJson = (objectKey: String, thisTopic: VideoTopic) => {
      val itemJson = new JsonObject
      itemJson.addProperty("name", thisTopic.name)
      itemJson.addProperty("confidence", thisTopic.confidence)
      itemJson.addProperty("language", thisTopic.language.toLanguageTag)
      json.add(objectKey, itemJson)
    }

    createJson("topic", topic)
    topic.parent.foreach(parent => {
      createJson("parentTopic", parent)
      parent.parent.map(grandparent => {
        createJson("grandparentTopic", grandparent)
      })
    })
  }
}
