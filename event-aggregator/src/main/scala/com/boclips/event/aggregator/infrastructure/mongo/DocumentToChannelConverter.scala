package com.boclips.event.aggregator.infrastructure.mongo

import java.time.Period
import java.util.Locale

import com.boclips.event.aggregator.domain.model._
import org.bson.Document

object DocumentToChannelConverter {
  def convert(document: Document): Channel = {
    val details = document.getObject("details")
    val ingest = document.getObject("ingest")
    val pedagogy = document.getObject("pedagogy")
    val marketing = document.getObject("marketing")
    Channel(
      id = ChannelId(document.getString("_id")),
      name = document.getString("name"),
      details = ChannelDetails(
        contentTypes = details.getListOption("contentTypes"),
        contentCategories = details.getListOption("contentCategories"),
        language = details.getStringOption("language").map(Locale.forLanguageTag),
        hubspotId = details.getStringOption("hubspotId"),
        contractId = details.getStringOption("contractId"),
        awards = details.getStringOption("awards"),
        notes = details.getStringOption("notes")
      ),
      ingest = ChannelIngest(
        _type = ingest.getString("type"),
        deliveryFrequency = ingest.getStringOption("deliveryFrequency").map(Period.parse)
      ),
      pedagogy = ChannelPedagogy(
        subjectNames = pedagogy.getListOption("subjectNames"),
        ageRangeMin = pedagogy.getIntOption("ageRangeMin"),
        ageRangeMax = pedagogy.getIntOption("ageRangeMax"),
        bestForTags = pedagogy.getListOption("bestForTags"),
        curriculumAligned = pedagogy.getStringOption("curriculumAligned"),
        educationalResources = pedagogy.getStringOption("educationalResources"),
        transcriptProvided = pedagogy.getBooleanOption("transcriptProvided")
      ),
      marketing = ChannelMarketing(
        status = marketing.getStringOption("status"),
        oneLineIntro = marketing.getStringOption("oneLineIntro"),
        logos = marketing.getListOption("logos"),
        showreel = marketing.getStringOption("showreel"),
        sampleVideos = marketing.getListOption("sampleVideos")
      )
    )
  }
}
