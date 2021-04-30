package com.boclips.event.aggregator.testsupport.testfactories

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.{Date, UUID}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.events._
import com.boclips.event.aggregator.domain.model.search.Query
import com.boclips.event.aggregator.domain.model.users.{BoclipsUserIdentity, UserId, UserIdentity}
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity
import com.boclips.event.infrastructure.EventFields
import org.bson.Document
import org.bson.types.ObjectId

import scala.collection.JavaConverters._

object EventFactory {

  def createVideoAddedToCollectionEvent(
                                         timestamp: ZonedDateTime = ZonedDateTime.now(),
                                         userIdentity: BoclipsUserIdentity = createBoclipsUserIdentity(),
                                         videoId: String = "videoId",
                                         query: Option[String] = None
                                       ): VideoAddedToCollectionEvent = {
    VideoAddedToCollectionEvent(
      timestamp = timestamp,
      userIdentity = userIdentity,
      url = None,
      videoId = VideoId(videoId),
      query = query.map(Query)
    )
  }

  def createPageRenderedDocument(
                                  timestamp: ZonedDateTime = ZonedDateTime.now(),
                                  userId: String = "user666",
                                  url: String = "the/url",
                                  viewportWidth: Int = 10,
                                  viewportHeight: Int = 20,
                                ): Document = {
    val properties = Map[String, Object](
      (EventFields.TYPE, "PAGE_RENDERED"),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.USER_ID, userId),
      (EventFields.URL, url),
      (EventFields.VIEWPORT_WIDTH, Int.box(viewportWidth)),
      (EventFields.VIEWPORT_HEIGHT, Int.box(viewportHeight)),
    )
    new Document(properties.asJava)
  }

  def createPageRenderedEvent(
                               timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                               userIdentity: UserIdentity = createBoclipsUserIdentity(),
                               viewportWidth: Option[Int] = Some(10),
                               viewportHeight: Option[Int] = Some(20),
                               url: String = "http://test.com"): PageRenderedEvent = {
    PageRenderedEvent(
      timestamp = timestamp,
      userIdentity = userIdentity,
      url = Some(Url.parse(url)),
      viewportWidth = viewportWidth,
      viewportHeight = viewportHeight,
    )

  }

  def createArbitraryEventDocument(
                                    eventType: String,
                                    userId: String = "userId",
                                    timestamp: ZonedDateTime = ZonedDateTime.now()
                                  ): Document = {

    new Document(Map[String, Object](
      (EventFields.TYPE, eventType),
      (EventFields.USER_ID, userId),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant))
    ).asJava)
  }

  def createVideoSegmentPlayedEventDocument(
                                             id: String = new ObjectId().toHexString,
                                             userId: Option[String] = Some("userId"),
                                             externalUserId: Option[String] = None,
                                             url: String = "http://example.com",
                                             query: Option[String] = None,
                                             videoId: String = "videoId",
                                             videoIndex: Option[Int] = None,
                                             timestamp: ZonedDateTime = ZonedDateTime.now(),
                                             segmentStartSeconds: Long = 0,
                                             segmentEndSeconds: Long = 10,
                                             deviceId: Option[String] = None,
                                           ): Document = {

    val properties = Map[String, Object](
      ("_id", new ObjectId(id)),
      (EventFields.TYPE, EventFields.Type.VIDEO_SEGMENT_PLAYED),
      (EventFields.URL, url),
      (EventFields.VIDEO_ID, videoId),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.PLAYBACK_SEGMENT_START_SECONDS, Long.box(segmentStartSeconds)),
      (EventFields.PLAYBACK_SEGMENT_END_SECONDS, Long.box(segmentEndSeconds)),
    ) ++ (
      userId.map(it => (EventFields.USER_ID, it))
        ++ externalUserId.map(it => (EventFields.EXTERNAL_USER_ID, it))
        ++ deviceId.map(device => (EventFields.DEVICE_ID, device))
        ++ query.map(q => (EventFields.SEARCH_QUERY, q))
        ++ videoIndex.map(index => (EventFields.PLAYBACK_VIDEO_INDEX, Int.box(index)))
      )

    new Document(properties.asJava)
  }

  def createVideoSegmentPlayedEvent(
                                     id: String = UUID.randomUUID().toString,
                                     timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                     userIdentity: UserIdentity = createBoclipsUserIdentity(),
                                     videoId: String = "videoId",
                                     url: Url = Url.parse("http://example.com/"),
                                     query: Option[String] = None,
                                     refererId: Option[String] = None,
                                     videoIndex: Option[Int] = None,
                                     secondsWatched: Int = 10
                                   ): VideoSegmentPlayedEvent = {
    VideoSegmentPlayedEvent(
      id = id,
      timestamp = timestamp,
      userIdentity = userIdentity,
      url = Option(url),
      query = query.map(Query),
      refererId = refererId.map(UserId),
      videoId = VideoId(videoId),
      videoIndex = videoIndex,
      secondsWatched = secondsWatched
    )
  }


  def createVideosSearchEventDocument(userId: String = "userId",
                                      query: String = "query",
                                      page: java.lang.Integer = 1,
                                      timestamp: ZonedDateTime = ZonedDateTime.now(),
                                      pageVideoIds: List[String] = List("id1", "id2"),
                                      totalResults: Long = 6669,
                                      queryParams: Option[Map[String, List[String]]] = None,
                                     ): Document = {
    new Document(Map[String, Object](
      (EventFields.TYPE, EventFields.Type.VIDEOS_SEARCHED),
      (EventFields.SEARCH_RESULTS_PAGE_VIDEO_IDS, pageVideoIds.asJava),
      (EventFields.USER_ID, userId),
      (EventFields.SEARCH_QUERY, query),
      (EventFields.SEARCH_RESULTS_PAGE_INDEX, page),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.SEARCH_RESULTS_TOTAL, Long.box(totalResults)),
      (EventFields.SEARCH_QUERY_PARAMS, queryParams match {
        case None => null
        case Some(queryParams) => queryParams.mapValues(x => x.asJava).asJava
      })
    ).asJava)
  }

  def createVideosSearchedEvent(
                                 timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                 userIdentity: BoclipsUserIdentity = createBoclipsUserIdentity(),
                                 query: String = "query",
                                 videoResults: Option[Iterable[String]] = None,
                                 pageIndex: Int = 0,
                                 totalResults: Int = 6669,
                                 url: Url = Url.parse("https://teachers.boclips.com/videos?page=1&q=shark"),
                                 queryParams: collection.mutable.Map[String, Iterable[String]] = null,
                               ): VideosSearchedEvent = {
    VideosSearchedEvent(
      timestamp = timestamp,
      userIdentity = userIdentity,
      url = Some(url),
      query = Query(query),
      videoResults = videoResults.map(_.map(VideoId)),
      pageIndex = pageIndex,
      totalResults = totalResults,
      queryParams = Option(queryParams)
    )
  }

  def createUserActivatedEventDocument(
                                        timestamp: ZonedDateTime = ZonedDateTime.now(),
                                        userId: String = "userId",
                                        totalUsers: Long = 100,
                                        activatedUsers: Long = 50
                                      ): Document = {
    val properties = Map[String, Object](
      (EventFields.TYPE, "USER_ACTIVATED"),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.USER_ID, userId),
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
      (EventFields.TYPE, EventFields.Type.VIDEO_INTERACTED_WITH),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.USER_ID, userId),
      (EventFields.URL, url),
      (EventFields.VIDEO_ID, videoId),
      (EventFields.SUBTYPE, subtype),
    )
    new Document(properties.asJava)
  }

  def createVideoInteractedWithEvent(
                                      timestamp: ZonedDateTime = ZonedDateTime.now(),
                                      userIdentity: UserIdentity = createBoclipsUserIdentity(),
                                      videoId: String = "videoId",
                                      query: Option[String] = None,
                                      subtype: Option[String] = Some("VIDEO_STARED_AT"),
                                      url: String = "http://test.com"
                                    ): VideoInteractedWithEvent = {
    VideoInteractedWithEvent(
      timestamp = timestamp,
      url = Some(Url.parse(url)),
      userIdentity = userIdentity,
      videoId = VideoId(videoId),
      query = query.map(Query),
      subtype = subtype,
    )
  }

  def createPlatformInteractedWithEventDocument(
                                                 timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                                 userId: String = "User-666",
                                                 subtype: String = "MODAL_CLICKED",
                                                 url: String = "http://test.com",
                                               ): Document = {
    val documentProperties = Map[String, Object](
      (EventFields.TYPE, "PLATFORM_INTERACTED_WITH"),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.USER_ID, userId),
      (EventFields.SUBTYPE, subtype),
      (EventFields.URL, url)
    )
    new Document(documentProperties.asJava)
  }

  def createAnonymousPlatformInteractedWithEventDocument(
                                                          timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                                          subtype: String = "MODAL_CLICKED",
                                                          url: String = "http://test.com",
                                                        ): Document = {
    val documentProperties = Map[String, Object](
      (EventFields.TYPE, "PLATFORM_INTERACTED_WITH"),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.SUBTYPE, subtype),
      (EventFields.URL, url)
    )
    new Document(documentProperties.asJava)
  }

  def createPlatformInteractedWithEvent(
                                         timestamp: ZonedDateTime = ZonedDateTime.now(),
                                         userIdentity: UserIdentity = createBoclipsUserIdentity(),
                                         subtype: Option[String] = Some("BANNER_CLICKED"),
                                         url: String = "http://test.com",
                                       ): PlatformInteractedWithEvent = {
    PlatformInteractedWithEvent(
      timestamp = timestamp,
      userIdentity = userIdentity,
      subtype = subtype,
      url = Some(Url.parse(url))
    )
  }

  def createCollectionsSearchedEvent(
                                      timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
                                      userIdentity: UserIdentity = createBoclipsUserIdentity(),
                                      query: String = "query",
                                      collectionResults: Iterable[String] = None,
                                      pageIndex: Int = 0,
                                      pageSize: Int = 10,
                                      totalResults: Int = 6669
                                    ): Event = {

    CollectionSearchedEvent(
      timestamp = timestamp,
      userIdentity = userIdentity,
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
      (EventFields.TYPE, EventFields.Type.RESOURCES_SEARCHED),
      (EventFields.SEARCH_RESOURCE_TYPE, "COLLECTION"),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.USER_ID, userId),
      (EventFields.URL, url),
      (EventFields.SEARCH_QUERY, query),
      (EventFields.SEARCH_RESULTS_PAGE_INDEX, Int.box(pageIndex)),
      (EventFields.SEARCH_RESULTS_PAGE_SIZE, Int.box(pageSize)),
      (EventFields.SEARCH_RESULTS_PAGE_RESOURCE_IDS, collectionIds.asJava),
      (EventFields.SEARCH_RESULTS_TOTAL, Long.box(totalResults)),
    ).asJava)
  }

  def createCollectionInteractedWithEvent(timestamp: ZonedDateTime = ZonedDateTime.now(),
                                          userIdentity: UserIdentity = createBoclipsUserIdentity(),
                                          collectionId: String = "collectionId",
                                          query: Option[String] = None,
                                          subtype: Option[String] = None,
                                          url: String = "http://test.com"): CollectionInteractedWithEvent = {
    CollectionInteractedWithEvent(
      timestamp = timestamp,
      userIdentity = userIdentity,
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
      (EventFields.TYPE, EventFields.Type.COLLECTION_INTERACTED_WITH),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.USER_ID, userId),
      (EventFields.URL, url),
      (EventFields.COLLECTION_ID, collectionId),
      (EventFields.SUBTYPE, subtype),
    )
    new Document(properties.asJava)

  }


  def createVideoAddedToCollectionDocument(
                                            timestamp: ZonedDateTime = ZonedDateTime.now(),
                                            userId: String = "user666",
                                            url: String = "the/url",
                                            videoId: String = "video-id"
                                          ): Document = {
    val properties = Map[String, Object](
      (EventFields.TYPE, EventFields.Type.VIDEO_ADDED_TO_COLLECTION),
      (EventFields.TIMESTAMP, Date.from(timestamp.toInstant)),
      (EventFields.USER_ID, userId),
      (EventFields.URL, url),
      (EventFields.VIDEO_ID, videoId)
    )
    new Document(properties.asJava)
  }

}
