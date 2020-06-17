package com.boclips.event.aggregator.testsupport.testfactories

import java.util.Currency

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.contentpartners.{Contract, ContractCosts, ContractDates, ContractId, ContractRestrictions, ContractRoyaltySplit}
import com.boclips.event.aggregator.presentation.formatters.schema.base.ExampleInstance

object ContractFactory {
  def createFullContract(
                      id: String = "contract-id",
                      channelName: String = "channel name",
                      contractDocumentLink: String = "http://google.com",
                      contractIsRolling: Boolean = true,
                      contractDates: ContractDates = ExampleInstance.create[ContractDates],
                      daysBeforeTerminationWarning: Int = 30,
                      yearsForMaximumLicense: Int = 90,
                      daysForSellOffPeriod: Int = 100,
                      royaltySplit: ContractRoyaltySplit = ExampleInstance.create[ContractRoyaltySplit],
                      minimumPriceDescription: String = "minimum",
                      remittanceCurrency: Currency = Currency.getInstance("GBP"),
                      restrictions: ContractRestrictions = ExampleInstance.create[ContractRestrictions],
                      costs: ContractCosts = ExampleInstance.create[ContractCosts]
                    ): Contract =
    Contract(
      id = ContractId(id),
      name = channelName,
      contractDocumentLink = Some(contractDocumentLink),
      contractIsRolling = Some(contractIsRolling),
      contractDates = Some(contractDates),
      daysBeforeTerminationWarning = Some(daysBeforeTerminationWarning),
      yearsForMaximumLicense = Some(yearsForMaximumLicense),
      daysForSellOffPeriod = Some(daysForSellOffPeriod),
      royaltySplit = Some(royaltySplit),
      minimumPriceDescription = Some(minimumPriceDescription),
      remittanceCurrency = Some(remittanceCurrency),
      restrictions = Some(restrictions),
      costs = costs
    )

  def createEmptyContract(
                         id: String = "contract-id",
                         channelName: String = "channel name"
                         ): Contract =
    Contract(
      id = ContractId(id),
      name = channelName,
      contractDocumentLink = None,
      contractIsRolling = None,
      contractDates = None,
      daysBeforeTerminationWarning = None,
      yearsForMaximumLicense = None,
      daysForSellOffPeriod = None,
      royaltySplit = None,
      minimumPriceDescription = None,
      remittanceCurrency = None,
      restrictions = None,
      costs = ContractCosts(
        minimumGuarantee = List(),
        upfrontLicense = None,
        technicalFee = None,
        recoupable = None
      )
    )
}
