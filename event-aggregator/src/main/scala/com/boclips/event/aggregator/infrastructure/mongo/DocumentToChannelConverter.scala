package com.boclips.event.aggregator.infrastructure.mongo

import java.time.Period
import java.util.Locale

import com.boclips.event.aggregator.domain.model.contentpartners._
import com.boclips.event.infrastructure.channel.{ChannelDocument, DistributionMethodDocument}

import scala.collection.JavaConverters._

object DocumentToChannelConverter {
  def convert(document: ChannelDocument): Channel = {
    val details = document.getDetails
    val ingest = document.getIngest
    val pedagogy = document.getPedagogy
    val marketing = document.getMarketing
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
        deliveryFrequency = Option(ingest.getDeliveryFrequency).map(Period.parse),
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
      )
    )
  }
}
