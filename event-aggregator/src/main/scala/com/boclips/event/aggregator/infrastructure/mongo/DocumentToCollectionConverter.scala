package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{collections, _}
import com.boclips.event.aggregator.domain.model.collections.{Collection, CollectionId}
import com.boclips.event.aggregator.domain.model.users.UserId
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.infrastructure.collection.CollectionDocument

import scala.collection.JavaConverters._

object DocumentToCollectionConverter {

  def convert(document: CollectionDocument): Collection = {
    val ageRange = AgeRange(
      Option(document.getMinAge).map(_.asInstanceOf[Int]),
      Option(document.getMaxAge).map(_.asInstanceOf[Int])
    )

    val createdTime = ZonedDateTime.ofInstant(
      document.getCreatedTime.toInstant,
      ZoneOffset.UTC
    )
    val updatedTime = ZonedDateTime.ofInstant(
      document.getUpdatedTime.toInstant,
      ZoneOffset.UTC
    )

    collections.Collection(
      id = CollectionId(document.getId),
      title = document.getTitle,
      description = document.getDescription,
      subjects = document.getSubjects.asScala.toList,
      ageRange = ageRange,
      videoIds = document.getVideoIds.asScala.toList.map(VideoId),
      ownerId = UserId(document.getOwnerId),
      bookmarks = document.getBookmarks.asScala.toList.map(UserId),
      createdTime = createdTime,
      updatedTime = updatedTime,
      public = document.getDiscoverable,
      deleted = document.getDeleted,
    )
  }
}
