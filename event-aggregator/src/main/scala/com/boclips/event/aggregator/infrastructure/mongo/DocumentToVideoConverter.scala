package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.contentpartners.{CategoryWithAncestors, ChannelId}
import com.boclips.event.aggregator.domain.model.videos._
import com.boclips.event.aggregator.domain.model.{videos, _}
import com.boclips.event.infrastructure.channel.CategoryWithAncestorsDocument
import com.boclips.event.infrastructure.video.{VideoAssetDocument, VideoDocument, VideoTopicDocument}

import java.time.{Duration, LocalDate, ZonedDateTime}
import java.util.Locale
import scala.collection.JavaConverters._
import scala.collection.mutable

object DocumentToVideoConverter {

  def convert(document: VideoDocument): Video = {
    videos.Video(
      id = VideoId(document.getId),
      title = document.getTitle,
      description = document.getDescription,
      channelId = ChannelId(document.getChannelId),
      playbackProvider = document.getPlaybackProviderType,
      playbackId = document.getPlaybackId,
      contentType = Option(document.getType),
      subjects = document.getSubjects.asScala.map(name => Subject(name)).toList,
      duration = Duration.ofSeconds(document.getDurationSeconds.toLong),
      releasedOn = LocalDate.parse(document.getReleasedOn),
      ingestedAt = ZonedDateTime.parse(document.getIngestedAt),
      assets = document.getAssets match {
        case null => List()
        case assetDocuments => assetDocuments.asScala.map(convertAsset).toList
      },
      originalDimensions = (document.getOriginalWidth, document.getOriginalHeight) match {
        case (null, null) => None
        case (width, height) => Some(Dimensions(width, height))
      },
      ageRange = AgeRange(integerOption(document.getAgeRangeMin), integerOption(document.getAgeRangeMax)),
      promoted = document.getPromoted,
      topics = document.getTopics.asScala.toList.map(convertTopic),
      keywords = Option(document.getKeywords).map(_.asScala.toList).getOrElse(Nil),
      sourceVideoReference = Option(document.getSourceVideoReference),
      deactivated = document.getDeactivated,
      categories = convertVideoCategories(Option(document.getCategories.asScala)),
    )
  }

  private def convertVideoCategories(categories: Option[collection.Map[String,java.util.Set[CategoryWithAncestorsDocument]]]) : Option[collection.mutable.Map[String, mutable.Set[CategoryWithAncestors]]] = {
      categories match {
        case None => None
        case Some(categoriesMap) if categoriesMap.isEmpty => None
        case Some(categoriesMap) =>
        val valuesAsMap = categoriesMap.mapValues(value => value.asScala).map(identity)
        val serializedSet = valuesAsMap.mapValues(value => value.map(it => convertCategoryWithAncestorsDocument(it))).map(identity)
        Some(collection.mutable.Map(serializedSet.toSeq:_*))
      }
  }

  private def convertCategoryWithAncestorsDocument(document: CategoryWithAncestorsDocument): CategoryWithAncestors = {
    CategoryWithAncestors(
      code = Option(document.getCode),
      description = Option(document.getDescription),
      ancestors = Option(document.getAncestors).map(_.asScala.toSet),
    )
  }


  private def convertAsset(document: VideoAssetDocument): VideoAsset = {
    VideoAsset(
      sizeKb = document.getSizeKb,
      bitrateKbps = document.getBitrateKbps,
      dimensions = Dimensions(document.getWidth, document.getHeight)
    )
  }

  private def convertTopic(document: VideoTopicDocument): VideoTopic = {
    val convert = (singleTopicDocument: VideoTopicDocument) => VideoTopic(
      name = singleTopicDocument.getName,
      confidence = singleTopicDocument.getConfidence,
      language = Locale.forLanguageTag(singleTopicDocument.getLanguage),
      parent = None
    )

    var tempChain: List[Option[VideoTopic]] = List()
    var currentTopic: Option[VideoTopicDocument] = Some(document)
    while (currentTopic.isDefined) {
      tempChain = currentTopic.map(convert) :: tempChain
      currentTopic = currentTopic.flatMap(it => Option(it.getParent))
    }
    val convertedChain: List[VideoTopic] = tempChain.flatten

    convertedChain.reduceLeft((childTopic: VideoTopic, parentTopic: VideoTopic) => {
      parentTopic.copy(parent = Some(childTopic))
    })
  }
}
