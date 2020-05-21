package com.boclips.event.aggregator.domain.model

import java.time.Period
import java.util.Locale

case class ChannelId(value: String) extends Ordered[ChannelId] {
  override def compare(that: ChannelId): Int = value.compare(that.value)
}

case class Channel(
                    id: ChannelId,
                    name: String,
                    details: ChannelDetails,
                    ingest: ChannelIngest,
                    pedagogy: ChannelPedagogy,
                    marketing: ChannelMarketing
                  )

case class ChannelDetails(
                           contentTypes: Option[List[String]],
                           contentCategories: Option[List[String]],
                           language: Option[Locale],
                           hubspotId: Option[String],
                           contractId: Option[String],
                           awards: Option[String],
                           notes: Option[String]
                         )

case class ChannelIngest(
                          _type: String,
                          deliveryFrequency: Option[Period]
                        )

case class ChannelPedagogy(
                            subjectNames: Option[List[String]],
                            ageRangeMin: Option[Int],
                            ageRangeMax: Option[Int],
                            bestForTags: Option[List[String]],
                            curriculumAligned: Option[String],
                            educationalResources: Option[String],
                            transcriptProvided: Option[Boolean]
                          )

case class ChannelMarketing(
                             status: Option[String],
                             oneLineIntro: Option[String],
                             logos: Option[List[String]],
                             showreel: Option[String],
                             sampleVideos: Option[List[String]]
                           )
