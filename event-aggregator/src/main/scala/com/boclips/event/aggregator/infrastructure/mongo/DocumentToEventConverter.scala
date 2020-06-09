package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events._
import org.bson.Document

object DocumentToEventConverter {

  implicit class DocumentExtensions(event: Document) {
    def url: Option[Url] = Option(event.getString("url")).map(Url.parse)

    def queryFromUrl: Option[Query] = url.flatMap(_.param("q")).map(Query)
  }


  def convert(event: Document): Event = {
    val eventType = event.getString("type")
    eventType match {
      case EventConstants.SEARCH | EventConstants.VIDEOS_SEARCHED => convertSearchEvent(event)
      case EventConstants.RESOURCES_SEARCHED => convertCollectionsSearched(event)
      case EventConstants.PLAYBACK | EventConstants.VIDEO_SEGMENT_PLAYED => convertVideoSegmentPlayedEvent(event)
      case EventConstants.VIDEO_INTERACTED_WITH => convertVideoInteractedWithEvent(event)
      case EventConstants.VIDEO_ADDED_TO_COLLECTION => convertVideoAddedToCollectionEvent(event)
      case EventConstants.PAGE_RENDERED => convertPageRenderedEvent(event)
      case EventConstants.COLLECTION_INTERACTED_WITH => convertCollectionInteractedWithEvent(event)
      case EventConstants.PLATFORM_INTERACTED_WITH => convertPlatformInteractedWithEvent(event)
      case _ => convertOtherEvent(event, eventType)
    }
  }

  def convertVideoSegmentPlayedEvent(document: Document): Event = {
    val url = document.url
    val userId = convertUserOrAnonymous(document)
    val videoIndex = document.getInteger("videoIndex") match {
      case null => None
      case index => Some(index.toInt)
    }

    val videoId = VideoId(Option(document.getString("assetId")).getOrElse {
      document.getString("videoId")
    })

    if (videoId.value == null) {
      convertOtherEvent(document, "OTHER_PLAYBACK")
    } else {
      VideoSegmentPlayedEvent(
        id = document.getObjectId("_id").toHexString,
        timestamp = ZonedDateTime.ofInstant(document.getDate("timestamp").toInstant, ZoneOffset.UTC),
        userId = userId,
        url = url,
        query = url.flatMap(_.param("q")).map(Query),
        refererId = url.flatMap(_.param("referer")).map(UserId),
        deviceId = Option(document.getString("playbackDevice")).map(DeviceId),
        videoId = videoId,
        videoIndex = videoIndex,
        secondsWatched = Math.max(0, document.getLong("segmentEndSeconds").toInt - document.getLong("segmentStartSeconds").toInt)
      )
    }
  }

  def convertSearchEvent(document: Document): Event = {
    val query = document.getString("query")
    if (query == null) {
      convertOtherEvent(document, "OTHER_SEARCH")
    } else {
      VideosSearchedEvent(
        timestamp = ZonedDateTime.ofInstant(document.getDate("timestamp").toInstant, ZoneOffset.UTC),
        userId = UserId(document.getString("userId")),
        query = Query(query),
        url = document.url,
        videoResults = document.getListOption[String]("pageVideoIds").map(_.map(VideoId)),
        pageIndex = document.getInteger("pageIndex", 0),
        totalResults = document.getLong("totalResults").toInt
      )
    }
  }

  def convertCollectionsSearched(document: Document): Event = {
    CollectionSearchedEvent(
      timestamp = ZonedDateTime.ofInstant(document.getDate("timestamp").toInstant, ZoneOffset.UTC),
      query = Query(document.getString("query")),
      userId = UserId(document.getString("userId")),
      url = document.url,
      collectionResults = document.getScalaList[String]("pageResourceIds").map(CollectionId),
      pageIndex = document.getInteger("pageIndex"),
      pageSize = document.getInteger("pageSize"),
      totalResults = document.getLong("totalResults").toInt
    )

  }

  def convertOtherEvent(document: Document, eventType: String) = OtherEvent(
    timestamp = ZonedDateTime.ofInstant(document.getDate("timestamp").toInstant, ZoneOffset.UTC),
    userId = UserId(document.getString("userId")),
    typeName = eventType
  )

  def convertVideoInteractedWithEvent(document: Document): VideoInteractedWithEvent = {
    val timestamp = ZonedDateTime.ofInstant(document.getDate("timestamp").toInstant, ZoneOffset.UTC)
    val url = document.url
    VideoInteractedWithEvent(
      url = url,
      timestamp = timestamp,
      userId = UserId(document.getString("userId")),
      videoId = VideoId(document.getString("videoId")),
      query = document.queryFromUrl,
      subtype = document.getStringOption("subtype")
    )
  }

  def convertCollectionInteractedWithEvent(document: Document): CollectionInteractedWithEvent = {
    val timestamp = ZonedDateTime.ofInstant(document.getDate("timestamp").toInstant, ZoneOffset.UTC)
    val url = document.url

    CollectionInteractedWithEvent(
      url = url,
      timestamp = timestamp,
      userId = UserId(document.getString("userId")),
      query = document.queryFromUrl,
      subtype = document.getStringOption("subtype"),
      collectionId = CollectionId(document.getString("collectionId"))
    )
  }


  def convertVideoAddedToCollectionEvent(document: Document): VideoAddedToCollectionEvent = {
    val timestamp = ZonedDateTime.ofInstant(document.getDate("timestamp").toInstant, ZoneOffset.UTC)
    VideoAddedToCollectionEvent(
      timestamp = timestamp,
      userId = UserId(document.getString("userId")),
      videoId = VideoId(document.getString("videoId")),
      url = document.url,
      query = document.queryFromUrl
    )
  }

  def convertPageRenderedEvent(document: Document): PageRenderedEvent = {
    val timestamp = ZonedDateTime.ofInstant(document.getDate("timestamp").toInstant, ZoneOffset.UTC)
    PageRenderedEvent(
      timestamp = timestamp,
      userId = UserId(document.getString("userId")),
      url = document.url
    )
  }

  def convertPlatformInteractedWithEvent(document: Document): PlatformInteractedWithEvent = {
    val userId = convertUserOrAnonymous(document)
    val timestamp = ZonedDateTime.ofInstant(document.getDate("timestamp").toInstant, ZoneOffset.UTC)
    PlatformInteractedWithEvent(
      userId = userId,
      timestamp = timestamp,
      url = document.url,
      subtype = document.getStringOption("subtype"),
    )
  }

  def convertUserOrAnonymous(document: Document): UserId = {
    val userId = document.get("userId") match {
      case null => EventConstants.anonymousUserId
      case value => UserId(value.toString)
    }
    userId
  }
}
