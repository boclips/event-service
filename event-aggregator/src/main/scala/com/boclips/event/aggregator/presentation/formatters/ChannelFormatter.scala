package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.contentpartners.Channel
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object ChannelFormatter extends SingleRowFormatter[Channel] {
  override def writeRow(obj: Channel, json: JsonObject): Unit = {
    json.addProperty("id", obj.id.value)
    json.addProperty("name", obj.name)

    json.addStringArrayProperty("detailsContentTypes", obj.details.contentTypes.getOrElse(Nil))
    json.addStringArrayProperty("detailsContentCategories", obj.details.contentCategories.getOrElse(Nil))
    json.addProperty("detailsLanguage", obj.details.language.map(_.toLanguageTag).orNull)
    json.addProperty("detailsHubspotId", obj.details.hubspotId.orNull)
    json.addProperty("detailsContractId", obj.details.contractId.orNull)
    json.addProperty("detailsAwards", obj.details.awards.orNull)
    json.addProperty("detailsNotes", obj.details.notes.orNull)

    json.addProperty("ingestType", obj.ingest._type)
    json.addProperty("ingestDeliveryFrequency", obj.ingest.deliveryFrequency.map(_.toString))
    json.addStringArrayProperty(
      "ingestDistributionMethods",
      obj.ingest.distributionMethods.map(_.map(it => it.toString)).getOrElse(Set())
    )

    json.addStringArrayProperty("pedagogySubjects", obj.pedagogy.subjectNames.getOrElse(Nil))
    json.addProperty("pedagogyAgeRangeMin", obj.pedagogy.ageRangeMin.map(Int.box).orNull)
    json.addProperty("pedagogyAgeRangeMax", obj.pedagogy.ageRangeMax.map(Int.box).orNull)
    json.addStringArrayProperty("pedagogyBestForTags", obj.pedagogy.bestForTags.getOrElse(Nil))
    json.addProperty("pedagogyCurriculumAligned", obj.pedagogy.curriculumAligned.orNull)
    json.addProperty("pedagogyEducationalResources", obj.pedagogy.educationalResources.orNull)
    json.addProperty("pedagogyTranscriptProvided", obj.pedagogy.transcriptProvided.map(Boolean.box).orNull)

    json.addProperty("marketingStatus", obj.marketing.status.orNull)
    json.addProperty("marketingOneLineIntro", obj.marketing.oneLineIntro.orNull)
    json.addStringArrayProperty("marketingLogos", obj.marketing.logos.getOrElse(Nil))

    val uniqueLogo: Option[String] = obj.marketing.logos match {
      case Some(value) => Some(value.head)
      case None => None
    }

    json.addProperty("marketingUniqueLogo", uniqueLogo.orNull)
    json.addProperty("marketingShowreel", obj.marketing.showreel.orNull)
    json.addStringArrayProperty("marketingSampleVideos", obj.marketing.sampleVideos.getOrElse(Nil))
  }
}
