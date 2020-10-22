package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.events._
import com.boclips.event.aggregator.domain.model.search.Query
import com.boclips.event.aggregator.domain.model.users._
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.infrastructure.EventFields
import org.bson.Document

object DocumentToEventConverter {

  final val LEGACY_ANONYMOUS_USER_ID: String = "anonymousUser"

  implicit class DocumentExtensions(event: Document) {
    def url: Option[Url] = Option(event.getString(EventFields.URL)).map(Url.parse)

    def queryFromUrl: Option[Query] = url.flatMap(_.param("q")).map(Query)

    def userIdentity: UserIdentity = {
      val userIdOption = Option(event.getString(EventFields.USER_ID))
        .filter(_ != LEGACY_ANONYMOUS_USER_ID)
        .map(UserId)
      val externalUserIdOption = Option(event.getString(EventFields.EXTERNAL_USER_ID))
        .map(ExternalUserId)
      val deviceIdOption = Option(event.getString(EventFields.DEVICE_ID))
        .map(DeviceId)
      val overrideUserIdOption = event.getBoolean("overrideUserId", false)

      (userIdOption, externalUserIdOption, deviceIdOption, overrideUserIdOption) match {
        case (Some(userId), Some(externalUserId), _, true) => BoclipsUserIdentity(boclipsId = UserId(externalUserId.value))
        case (Some(userId), None, _, _) => BoclipsUserIdentity(userId)
        case (Some(userId), Some(externalUserId), _, _) => ExternalUserIdentity(userId, externalUserId)
        case (_, _, deviceId, _) => AnonymousUserIdentity(deviceId)
      }
    }
  }

  def convert(event: Document): Event = {
    val eventType = event.getString(EventFields.TYPE)
    eventType match {
      case EventFields.Type.VIDEOS_SEARCHED => convertSearchEvent(event)
      case EventFields.Type.RESOURCES_SEARCHED => convertCollectionsSearched(event)
      case EventFields.Type.VIDEO_SEGMENT_PLAYED => convertVideoSegmentPlayedEvent(event)
      case EventFields.Type.VIDEO_INTERACTED_WITH => convertVideoInteractedWithEvent(event)
      case EventFields.Type.VIDEO_ADDED_TO_COLLECTION => convertVideoAddedToCollectionEvent(event)
      case EventFields.Type.PAGE_RENDERED => convertPageRenderedEvent(event)
      case EventFields.Type.COLLECTION_INTERACTED_WITH => convertCollectionInteractedWithEvent(event)
      case EventFields.Type.PLATFORM_INTERACTED_WITH => convertPlatformInteractedWithEvent(event)
      case _ => convertOtherEvent(event, eventType)
    }
  }

  def convertVideoSegmentPlayedEvent(document: Document): Event = {
    val url = document.url
    val userIdentity = document.userIdentity
    val videoIndex = document.getInteger(EventFields.PLAYBACK_VIDEO_INDEX) match {
      case null => None
      case index => Some(index.toInt)
    }

    val videoId = VideoId(document.getString(EventFields.VIDEO_ID))

    if (videoId.value == null) {
      convertOtherEvent(document, "OTHER_PLAYBACK")
    } else {
      VideoSegmentPlayedEvent(
        id = document.getObjectId("_id").toHexString,
        timestamp = ZonedDateTime.ofInstant(document.getDate(EventFields.TIMESTAMP).toInstant, ZoneOffset.UTC),
        userIdentity = userIdentity,
        url = url,
        query = url.flatMap(_.param("q")).map(Query),
        refererId = url.flatMap(_.param("referer")).map(UserId),
        videoId = videoId,
        videoIndex = videoIndex,
        secondsWatched = Math.max(0, document.getLong(EventFields.PLAYBACK_SEGMENT_END_SECONDS).toInt - document.getLong(EventFields.PLAYBACK_SEGMENT_START_SECONDS).toInt)
      )
    }
  }

  def convertSearchEvent(document: Document): Event = {
    val query = document.getString(EventFields.SEARCH_QUERY)
    if (query == null) {
      convertOtherEvent(document, "OTHER_SEARCH")
    } else {
      VideosSearchedEvent(
        timestamp = ZonedDateTime.ofInstant(document.getDate(EventFields.TIMESTAMP).toInstant, ZoneOffset.UTC),
        userIdentity = document.userIdentity,
        query = Query(query),
        url = document.url,
        videoResults = document.getListOption[String](EventFields.SEARCH_RESULTS_PAGE_VIDEO_IDS).map(_.map(VideoId)),
        pageIndex = document.getInteger(EventFields.SEARCH_RESULTS_PAGE_INDEX, 0),
        totalResults = document.getLong(EventFields.SEARCH_RESULTS_TOTAL).toInt
      )
    }
  }

  def convertCollectionsSearched(document: Document): Event = {
    CollectionSearchedEvent(
      timestamp = ZonedDateTime.ofInstant(document.getDate(EventFields.TIMESTAMP).toInstant, ZoneOffset.UTC),
      query = Query(document.getString(EventFields.SEARCH_QUERY)),
      userIdentity = document.userIdentity,
      url = document.url,
      collectionResults = document.getScalaList[String](EventFields.SEARCH_RESULTS_PAGE_RESOURCE_IDS).map(CollectionId),
      pageIndex = document.getInteger(EventFields.SEARCH_RESULTS_PAGE_INDEX),
      pageSize = document.getInteger(EventFields.SEARCH_RESULTS_PAGE_SIZE),
      totalResults = document.getLong(EventFields.SEARCH_RESULTS_TOTAL).toInt
    )

  }

  def convertOtherEvent(document: Document, eventType: String) = OtherEvent(
    timestamp = ZonedDateTime.ofInstant(document.getDate(EventFields.TIMESTAMP).toInstant, ZoneOffset.UTC),
    userIdentity = document.userIdentity,
    typeName = eventType
  )

  def convertVideoInteractedWithEvent(document: Document): VideoInteractedWithEvent = {
    val timestamp = ZonedDateTime.ofInstant(document.getDate(EventFields.TIMESTAMP).toInstant, ZoneOffset.UTC)
    val url = document.url
    VideoInteractedWithEvent(
      url = url,
      timestamp = timestamp,
      userIdentity = document.userIdentity,
      videoId = VideoId(document.getString(EventFields.VIDEO_ID)),
      query = document.queryFromUrl,
      subtype = document.getStringOption(EventFields.SUBTYPE)
    )
  }

  def convertCollectionInteractedWithEvent(document: Document): CollectionInteractedWithEvent = {
    val timestamp = ZonedDateTime.ofInstant(document.getDate(EventFields.TIMESTAMP).toInstant, ZoneOffset.UTC)
    val url = document.url

    CollectionInteractedWithEvent(
      url = url,
      timestamp = timestamp,
      userIdentity = document.userIdentity,
      query = document.queryFromUrl,
      subtype = document.getStringOption(EventFields.SUBTYPE),
      collectionId = CollectionId(document.getString(EventFields.COLLECTION_ID))
    )
  }


  def convertVideoAddedToCollectionEvent(document: Document): VideoAddedToCollectionEvent = {
    val timestamp = ZonedDateTime.ofInstant(document.getDate(EventFields.TIMESTAMP).toInstant, ZoneOffset.UTC)
    VideoAddedToCollectionEvent(
      timestamp = timestamp,
      userIdentity = document.userIdentity.asInstanceOf[BoclipsUserIdentity],
      videoId = VideoId(document.getString(EventFields.VIDEO_ID)),
      url = document.url,
      query = document.queryFromUrl
    )
  }

  def convertPageRenderedEvent(document: Document): PageRenderedEvent = {
    val timestamp = ZonedDateTime.ofInstant(document.getDate(EventFields.TIMESTAMP).toInstant, ZoneOffset.UTC)
    PageRenderedEvent(
      timestamp = timestamp,
      userIdentity = document.userIdentity,
      url = document.url
    )
  }

  def convertPlatformInteractedWithEvent(document: Document): PlatformInteractedWithEvent = {
    val timestamp = ZonedDateTime.ofInstant(document.getDate(EventFields.TIMESTAMP).toInstant, ZoneOffset.UTC)
    PlatformInteractedWithEvent(
      userIdentity = document.userIdentity,
      timestamp = timestamp,
      url = document.url,
      subtype = document.getStringOption(EventFields.SUBTYPE),
    )
  }
}
