package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.{AgeRange, Collection, CollectionId, UserId, VideoId}
import org.bson.Document

object DocumentToCollectionConverter {

  def convert(document: Document): Collection = {
    Collection(
      id = CollectionId(document.getString("_id")),
      title = document.getString("title"),
      description = document.getString("description"),
      subjects = document.getList[String]("subjects"),
      ageRange = AgeRange(document.getIntOption("ageRangeMin"), document.getIntOption("ageRangeMax")),
      videoIds = document.getList[String]("videoIds").map(VideoId),
      ownerId = UserId(document.getString("ownerId")),
      bookmarks = document.getList[String]("bookmarks").map(UserId),
      createdTime = document.getDateTime("createdTime"),
      updatedTime = document.getDateTime("updatedTime"),
      public = document.getBoolean("public"),
      deleted = document.getBoolean("deleted"),
    )
  }
}
