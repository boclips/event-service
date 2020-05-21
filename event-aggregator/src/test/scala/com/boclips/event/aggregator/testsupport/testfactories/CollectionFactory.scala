package com.boclips.event.aggregator.testsupport.testfactories

import java.time.ZonedDateTime
import java.util.Date

import com.boclips.event.aggregator.domain.model._
import org.bson.Document

import scala.collection.JavaConverters._

object CollectionFactory {

  def createCollection(
                        id: String = "123",
                        title: String = "title",
                        description: String = "description",
                        subjects: List[String] = List(),
                        ageRange: AgeRange = AgeRange(None, None),
                        videoIds: List[String] = List(),
                        ownerId: String = "owner",
                        bookmarks: List[String] = List(),
                        createdTime: ZonedDateTime = ZonedDateTime.now(),
                        updatedTime: ZonedDateTime = ZonedDateTime.now(),
                        deleted: Boolean = false,
                        public: Boolean = true
                      ): Collection =
    Collection(
      id = CollectionId(id),
      title = title,
      description = description,
      subjects = subjects,
      ageRange = ageRange,
      videoIds = videoIds.map(VideoId),
      ownerId = UserId(ownerId),
      bookmarks = bookmarks.map(UserId),
      createdTime = createdTime,
      updatedTime = updatedTime,
      public = public,
      deleted = deleted
    )

  def createCollectionDocument(
                                id: String = "123",
                                title: String = "title",
                                description: String = "description",
                                subjects: List[String] = List(),
                                ageRange: AgeRange = AgeRange(None, None),
                                videoIds: List[String] = List(),
                                ownerId: String = "owner",
                                bookmarks: List[String] = List(),
                                createdTime: ZonedDateTime = ZonedDateTime.now(),
                                updatedTime: ZonedDateTime = ZonedDateTime.now(),
                                deleted: Boolean = false,
                                public: Boolean = true
                              ): Document = {
    new Document(Map[String, Object](
      ("_id", id),
      ("title", title),
      ("description", description),
      ("subjects", subjects.asJava),
      ("ageRangeMin", ageRange.min.map(n => n.asInstanceOf[Integer]).orNull),
      ("ageRangeMax", ageRange.max.map(n => n.asInstanceOf[Integer]).orNull),
      ("videoIds", videoIds.asJava),
      ("ownerId", ownerId),
      ("bookmarks", bookmarks.asJava),
      ("createdTime", Date.from(createdTime.toInstant)),
      ("updatedTime", Date.from(updatedTime.toInstant)),
      ("deleted", deleted.asInstanceOf[java.lang.Boolean]),
      ("public", public.asInstanceOf[java.lang.Boolean])
    ).asJava)
  }

}
