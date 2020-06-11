package com.boclips.event.aggregator.testsupport.testfactories

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.{Date, UUID}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events._
import com.boclips.event.infrastructure.EventFields
import org.bson.Document
import org.bson.types.ObjectId

import scala.collection.JavaConverters._

object EventFactory {


  def createVideoAddedToCollectionEvent(
                                         timestamp: ZonedDateTime = ZonedDateTime.now(),
                                         userId: String = "user666",
                                         videoId: String = "videoId",
                                         query: Option[String] = None
                                       ): VideoAddedToCollectionEvent = {
    VideoAddedToCollectionEvent(
      timestamp = timestamp,
      userId = UserId(userId),
      url = None,
      videoId = VideoId(videoId),
      query = query.map(Query)
    )

  }


  def createPageRenderedDocument(
                                  timestamp: ZonedDateTime = ZonedDateTime.now(),
                                  userId: String = "user666",
                                  url: String = "the/url",
                                ): Document = {
    val properties = Map[String, Object](
      ("type", "PAGE_RENDERED"),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("userId", userId),
      ("url", url),
    )
    new Document(properties.asJava)
  }

  def createPageRenderedEvent(
                               timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                               userId: String = "userId",
                               url: String = "http://test.com"): PageRenderedEvent = {
    PageRenderedEvent(
      timestamp = timestamp,
      userId = UserId(userId),
      url = Some(Url.parse(url))
    )

  }

  def createArbitraryEventDocument(
                                    eventType: String,
                                    userId: String = "userId",
                                    timestamp: ZonedDateTime = ZonedDateTime.now()
                                  ): Document = {

    new Document(Map[String, Object](
      ("type", eventType),
      ("userId", userId),
      ("timestamp", Date.from(timestamp.toInstant))
    ).asJava)
  }

  def createVideoSegmentPlayedEventDocument(
                                             id: String = "5cded765a22ace42b322a49e",
                                             userId: String = "userId",
                                             url: String = "http://example.com",
                                             videoId: String = "videoId",
                                             videoIndex: Option[Int] = None,
                                             timestamp: ZonedDateTime = ZonedDateTime.now(),
                                             segmentStartSeconds: Long = 0,
                                             segmentEndSeconds: Long = 10,
                                             videoDurationSeconds: Long = 300,
                                             deviceId: Option[String] = None,
                                           ): Document = {

    val properties = Map[String, Object](
      ("_id", new ObjectId(id)),
      ("type", EventFields.Type.VIDEO_SEGMENT_PLAYED),
      ("userId", userId),
      ("url", url),
      ("videoId", videoId),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("segmentStartSeconds", Long.box(segmentStartSeconds)),
      ("segmentEndSeconds", Long.box(segmentEndSeconds)),
      ("videoDurationSeconds", Long.box(videoDurationSeconds))
    ) ++ (videoIndex.map(index => ("videoIndex", Int.box(index)))
      ++ deviceId.map(device => ("deviceId", device)))

    new Document(properties.asJava)
  }

  def createVideoSegmentPlayedEvent(
                                     id: String = UUID.randomUUID().toString,
                                     timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                     userId: String = "userId",
                                     videoId: String = "videoId",
                                     url: Url = Url.parse("http://example.com/"),
                                     query: Option[String] = None,
                                     refererId: Option[String] = None,
                                     videoIndex: Option[Int] = None,
                                     playbackDevice: Option[String] = None,
                                     secondsWatched: Int = 10
                                   ): VideoSegmentPlayedEvent = {
    VideoSegmentPlayedEvent(
      id = id,
      timestamp = timestamp,
      userId = UserId(userId),
      url = Option(url),
      query = query.map(Query),
      refererId = refererId.map(UserId),
      videoId = VideoId(videoId),
      deviceId = playbackDevice.map(DeviceId),
      videoIndex = videoIndex,
      secondsWatched = secondsWatched
    )
  }


  def createVideosSearchEventDocument(userId: String = "userId",
                                      query: String = "query",
                                      page: java.lang.Integer = 1,
                                      timestamp: ZonedDateTime = ZonedDateTime.now(),
                                      pageVideoIds: List[String] = List("id1", "id2"),
                                      totalResults: Long = 6669
                                     ): Document = {
    new Document(Map[String, Object](
      ("type", EventFields.Type.VIDEOS_SEARCHED),
      ("pageVideoIds", pageVideoIds.asJava),
      ("userId", userId),
      ("query", query),
      ("pageIndex", page),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("totalResults", Long.box(totalResults))
    ).asJava)
  }

  def createVideosSearchedEvent(
                                 timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                 userId: String = "userId",
                                 query: String = "query",
                                 videoResults: Option[Iterable[String]] = None,
                                 pageIndex: Int = 0,
                                 totalResults: Int = 6669,
                                 url: Url = Url.parse("https://teachers.boclips.com/videos?page=1&q=shark")
                               ): VideosSearchedEvent = {
    VideosSearchedEvent(
      timestamp = timestamp,
      userId = UserId(userId),
      url = Some(url),
      query = Query(query),
      videoResults = videoResults.map(_.map(VideoId)),
      pageIndex = pageIndex,
      totalResults = totalResults
    )
  }

  def createUserActivatedEventDocument(
                                        timestamp: ZonedDateTime = ZonedDateTime.now(),
                                        userId: String = "userId",
                                        totalUsers: Long = 100,
                                        activatedUsers: Long = 50
                                      ): Document = {
    val properties = Map[String, Object](
      ("type", "USER_ACTIVATED"),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("userId", userId),
      ("totalUsers", Long.box(totalUsers)),
      ("activatedUsers", Long.box(activatedUsers))
    )
    new Document(properties.asJava)
  }


  def createVideoInteractedWithEventDocument(
                                              timestamp: ZonedDateTime = ZonedDateTime.now(),
                                              userId: String = "userId",
                                              url: String = "url",
                                              videoId: String = "videoId",
                                              subtype: String = "VIDEO_STARED_AT"
                                            ): Document = {
    val properties = Map[String, Object](
      ("type", "VIDEO_INTERACTED_WITH"),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("userId", userId),
      ("url", url),
      ("videoId", videoId),
      ("subtype", subtype),
    )
    new Document(properties.asJava)
  }

  def createVideoInteractedWithEvent(
                                      timestamp: ZonedDateTime = ZonedDateTime.now(),
                                      userId: String = "userId",
                                      videoId: String = "videoId",
                                      query: Option[String] = None,
                                      subtype: Option[String] = Some("VIDEO_STARED_AT"),
                                      url: String = "http://test.com"
                                    ): VideoInteractedWithEvent = {
    VideoInteractedWithEvent(
      timestamp = timestamp,
      url = Some(Url.parse(url)),
      userId = UserId(userId),
      videoId = VideoId(videoId),
      query = query.map(Query),
      subtype = subtype,
    )
  }

  def createPlatformInteractedWithEventDocument(
                                               timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                               userId: String = "User-666",
                                               subtype: String= "MODAL_CLICKED",
                                               url: String = "http://test.com",
                                               ): Document = {
    val documentProperties = Map[String, Object](
      ("type", "PLATFORM_INTERACTED_WITH"),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("userId", userId),
      ("subtype", subtype),
      ("url", url)
    )
    new Document(documentProperties.asJava)
  }

  def createAnonymousPlatformInteractedWithEventDocument(
                                                 timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                                 subtype: String= "MODAL_CLICKED",
                                                 url: String = "http://test.com",
                                               ): Document = {
    val documentProperties = Map[String, Object](
      ("type", "PLATFORM_INTERACTED_WITH"),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("subtype", subtype),
      ("url", url)
    )
    new Document(documentProperties.asJava)
  }

  def createPlatformInteractedWithEvent(
                                         timestamp: ZonedDateTime = ZonedDateTime.now(),
                                         userId: String = "uid-1",
                                         subtype: Option[String] = Some("BANNER_CLICKED"),
                                         url: String = "http://test.com",
                                       ): PlatformInteractedWithEvent = {
    PlatformInteractedWithEvent(
      timestamp = timestamp,
      userId = UserId(userId),
      subtype = subtype,
      url = Some(Url.parse(url))
    )
  }

  def createCollectionsSearchedEvent(
                                      timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                      userId: String = "userId",
                                      query: String = "query",
                                      collectionResults: Iterable[String] = None,
                                      pageIndex: Int = 0,
                                      pageSize: Int = 10,
                                      totalResults: Int = 6669
                                    ): Event = {

    CollectionSearchedEvent(
      timestamp = timestamp,
      userId = UserId(userId),
      url = None,
      query = Query(query),
      collectionResults = collectionResults.map(CollectionId),
      pageIndex = pageIndex,
      pageSize = pageSize,
      totalResults = totalResults
    )
  }

  def createCollectionsSearchedEventDocument(
                                              timestamp: ZonedDateTime = ZonedDateTime.now(),
                                              userId: String = "user-id",
                                              url: String = "https://teachers.boclips.com",
                                              query: String = "query",
                                              pageIndex: Int = 0,
                                              pageSize: Int = 0,
                                              collectionIds: List[String] = List(),
                                              totalResults: Long = 100
                                            ): Document = {
    new Document(Map[String, Object](
      ("type", "RESOURCES_SEARCHED"),
      ("resourceType", "COLLECTION"),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("userId", userId),
      ("url", url),
      ("query", query),
      ("pageIndex", Int.box(pageIndex)),
      ("pageSize", Int.box(pageSize)),
      ("pageResourceIds", collectionIds.asJava),
      ("totalResults", Long.box(totalResults)),
    ).asJava)
  }

  def createCollectionInteractedWithEvent(timestamp: ZonedDateTime = ZonedDateTime.now(),
                                          userId: String = "userId",
                                          collectionId: String = "collectionId",
                                          query: Option[String] = None,
                                          subtype: Option[String] = None,
                                          url: String = "http://test.com"): CollectionInteractedWithEvent = {
    CollectionInteractedWithEvent(
      timestamp = timestamp,
      userId = UserId(userId),
      collectionId = CollectionId(collectionId),
      query = query.map(Query),
      subtype = subtype,
      url = Some(Url.parse(url))
    )
  }


  def createCollectionInteractedWithEventDocument(
                                                   timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                                   userId: String = "userId",
                                                   url: String = "http://test.com",
                                                   collectionId: String = "1234",
                                                   subtype: String = ""): Document = {
    val properties = Map[String, Object](
      ("type", "COLLECTION_INTERACTED_WITH"),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("userId", userId),
      ("url", url),
      ("collectionId", collectionId),
      ("subtype", subtype)
    )
    new Document(properties.asJava)

  }


  def createVideoAddedToCollectionDocument(
                                            timestamp: ZonedDateTime = ZonedDateTime.now(),
                                            userId: String = "user666",
                                            url: String = "the/url",
                                            videoId: String = "videoId"
                                          ): Document = {
    val properties = Map[String, Object](
      ("type", "VIDEO_ADDED_TO_COLLECTION"),
      ("timestamp", Date.from(timestamp.toInstant)),
      ("userId", userId),
      ("url", url),
      ("videoId", videoId)
    )
    new Document(properties.asJava)
  }

}
