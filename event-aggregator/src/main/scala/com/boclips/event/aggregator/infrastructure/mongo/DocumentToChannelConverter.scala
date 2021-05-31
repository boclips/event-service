package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.contentpartners._
import com.boclips.event.infrastructure.channel.{CategoryWithAncestorsDocument, ChannelDocument, DistributionMethodDocument}

import java.time.Period
import java.util.Locale
import scala.collection.JavaConverters._
import scala.collection.convert.ImplicitConversions.`set asScala`

object DocumentToChannelConverter {
  def convert(document: ChannelDocument): Channel = {
    val details = document.getDetails
    val ingest = document.getIngest
    val pedagogy = document.getPedagogy
    val marketing = document.getMarketing
    val categories = document.getCategories
    Channel(
      id = ChannelId(document.getId),
      name = document.getName,
      details = ChannelDetails(
        contentTypes = Option(details.getContentTypes).map(_.asScala.toList),
        contentCategories = Option(details.getContentCategories).map(_.asScala.toList),
        language = Option(details.getLanguage).map(Locale.forLanguageTag),
        contractId = Option(details.getContractId),
        notes = Option(details.getNotes)
      ),
      ingest = ChannelIngest(
        _type = ingest.getType,
        distributionMethods = Option(ingest.getDistributionMethods).map(_.asScala.map {
          case DistributionMethodDocument.STREAM => Streaming
          case DistributionMethodDocument.DOWNLOAD => Download
        }.toSet)
      ),
      pedagogy = ChannelPedagogy(
        subjectNames = Option(pedagogy.getSubjectNames).map(_.asScala.toList),
        ageRangeMin = integerOption(pedagogy.getAgeRangeMin),
        ageRangeMax = integerOption(pedagogy.getAgeRangeMax),
        bestForTags = Option(pedagogy.getBestForTags).map(_.asScala.toList)
      ),
      marketing = ChannelMarketing(
        status = Option(marketing.getStatus),
        oneLineIntro = Option(marketing.getOneLineIntro),
        logos = Option(marketing.getLogos).map(_.asScala.toList),
        showreel = Option(marketing.getShowreel),
        sampleVideos = Option(marketing.getSampleVideos).map(_.asScala.toList)
      ),
      categories = Option(categories.map(DocumentToCategoryWithAncestorsConverter.convert(_)).toSet
      )
    )
  }
}

object DocumentToCategoryWithAncestorsConverter{
  def convert(document: CategoryWithAncestorsDocument): CategoryWithAncestors = {
    CategoryWithAncestors(
      code = Option(document.getCode),
      description = Option(document.getDescription),
      ancestors = Option(document.getAncestors).map(_.asScala.toSet),
    )
  }
}
