package com.boclips.event.aggregator.testsupport.testfactories

import java.util.Currency

import com.boclips.event.aggregator.domain.model.ContractLegalRestriction
import com.boclips.event.aggregator.domain.model.contentpartners._
import com.boclips.event.aggregator.presentation.formatters.schema.base.ExampleInstance
import com.boclips.event.aggregator.presentation.model.ContractTableRow

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

  def createContractRestrictions(
                                  clientFacing: Option[List[String]] = None,
                                  territory: Option[String] = None,
                                  licensing: Option[String] = None,
                                  editing: Option[String] = None,
                                  marketing: Option[String] = None,
                                  companies: Option[String] = None,
                                  payout: Option[String] = None,
                                  other: Option[String] = None,
                                ): ContractRestrictions =
    ContractRestrictions(
      clientFacing = clientFacing,
      territory = territory,
      licensing = licensing,
      editing = editing,
      marketing = marketing,
      companies = companies,
      payout = payout,
      other = other
    )

  def createContractRestriction(
                                 id: String = "id-998",
                                 text: String = "rules allowed"
                               ): ContractLegalRestriction =
    ContractLegalRestriction(
      id = id,
      text = text
    )

  def createFullTableRowContract(
                                  contract: Contract = createFullContract(),
                                  clientFacingRestrictions: List[ContractLegalRestriction] = List(createContractRestriction())

  ) : ContractTableRow =
    ContractTableRow(
      contract = contract,
      clientFacingRestrictions = clientFacingRestrictions
    )
}
