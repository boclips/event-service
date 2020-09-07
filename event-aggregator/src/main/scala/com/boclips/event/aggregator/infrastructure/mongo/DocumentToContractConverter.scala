package com.boclips.event.aggregator.infrastructure.mongo

import java.util.Currency

import com.boclips.event.aggregator.domain.model.contentpartners
import com.boclips.event.aggregator.domain.model.contentpartners._
import com.boclips.event.infrastructure.contract.ContractDocument

import scala.collection.JavaConverters._

object DocumentToContractConverter {
  def convert(document: ContractDocument): Contract = {
    val costs = document.getCosts
    contentpartners.Contract(
      id = ContractId(document.getId),
      name = document.getName,
      contractDocumentLink = Option(document.getContractDocumentLink),
      contractIsRolling = Option(document.getContractIsRolling),
      contractDates = Option(document.getContractDates)
        .map(it => ContractDates(
          start = Option(it.getStart),
          end = Option(it.getEnd)
        )),
      daysBeforeTerminationWarning = integerOption(document.getDaysBeforeTerminationWarning),
      yearsForMaximumLicense = integerOption(document.getYearsForMaximumLicense),
      daysForSellOffPeriod = integerOption(document.getDaysForSellOffPeriod),
      royaltySplit = Option(document.getRoyaltySplit)
        .map(it => ContractRoyaltySplit(
          download = Option(it.getDownload),
          streaming = Option(it.getStreaming)
        )),
      minimumPriceDescription = Option(document.getMinimumPriceDescription),
      remittanceCurrency = Option(document.getRemittanceCurrency)
        .map(Currency.getInstance),
      restrictions = Option(document.getRestrictions)
        .map(it => ContractRestrictions(
          clientFacing = Option(it.getClientFacing).map(_.asScala.toList),
          territory = Option(it.getTerritory),
          licensing = Option(it.getLicensing),
          editing = Option(it.getEditing),
          marketing = Option(it.getMarketing),
          companies = Option(it.getCompanies),
          payout = Option(it.getPayout),
          other = Option(it.getOther)
        )),
      costs = ContractCosts(
        minimumGuarantee = Option(costs.getMinimumGuarantee)
          .map(_.asScala.toList.map(BigDecimal(_))).getOrElse(List()),
        upfrontLicense = Option(costs.getUpfrontLicense).map(BigDecimal(_)),
        technicalFee = Option(costs.getTechnicalFee).map(BigDecimal(_)),
        recoupable = booleanOption(costs.getRecoupable)
      )
    )
  }
}
