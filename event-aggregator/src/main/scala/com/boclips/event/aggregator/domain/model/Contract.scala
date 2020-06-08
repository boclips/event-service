package com.boclips.event.aggregator.domain.model

import java.time.LocalDate
import java.util.Currency

case class ContractId(value: String) extends Ordered[ContractId] {
  override def compare(that: ContractId): Int = value.compare(that.value)
}

case class Contract(
                     id: ContractId,
                     channelName: String,
                     contractDocumentLink: Option[String],
                     contractIsRolling: Option[Boolean],
                     contractDates: Option[ContractDates],
                     daysBeforeTerminationWarning: Option[Int],
                     yearsForMaximumLicense: Option[Int],
                     daysForSellOffPeriod: Option[Int],
                     royaltySplit: Option[ContractRoyaltySplit],
                     minimumPriceDescription: Option[String],
                     remittanceCurrency: Option[Currency],
                     restrictions: Option[ContractRestrictions],
                     costs: ContractCosts
                   )

case class ContractDates(
                          start: Option[LocalDate],
                          end: Option[LocalDate]
                        )

case class ContractRoyaltySplit(
                                 download: Option[Float],
                                 streaming: Option[Float]
                               )

case class ContractRestrictions(
                                 clientFacing: Option[List[String]],
                                 territory: Option[String],
                                 licensing: Option[String],
                                 editing: Option[String],
                                 marketing: Option[String],
                                 companies: Option[String],
                                 payout: Option[String],
                                 other: Option[String]
                               )

case class ContractCosts(
                          minimumGuarantee: List[BigDecimal],
                          upfrontLicense: Option[BigDecimal],
                          technicalFee: Option[BigDecimal],
                          recoupable: Option[Boolean]
                        )