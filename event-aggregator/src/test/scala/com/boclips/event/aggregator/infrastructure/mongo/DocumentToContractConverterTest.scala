package com.boclips.event.aggregator.infrastructure.mongo

import java.time.LocalDate
import java.util.Currency

import com.boclips.event.aggregator.domain.model.{ContractCosts, ContractDates, ContractId, ContractRestrictions, ContractRoyaltySplit}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.contract._

import scala.collection.JavaConverters._

class DocumentToContractConverterTest extends Test {
  it should "convert a full document" in {
    val document = ContractDocument.sample
      .id("contract-id")
      .name("channel name")
      .contractDocumentLink("http://mylink.com")
      .contractDates(ContractDatesDocument.sample
        .start(LocalDate.ofYearDay(2012, 300))
        .end(LocalDate.ofYearDay(2016, 310))
        .build()
      )
      .contractIsRolling(true)
      .daysBeforeTerminationWarning(60)
      .yearsForMaximumLicense(30)
      .daysForSellOffPeriod(199)
      .royaltySplit(ContractRoyaltySplitDocument.sample
        .download(20F)
        .streaming(0.9F)
        .build()
      )
      .minimumPriceDescription("minimum")
      .remittanceCurrency("GBP")
      .restrictions(ContractRestrictionsDocument.sample
        .clientFacing(List("client facing").asJava)
        .territory("territory")
        .licensing("licensing")
        .editing("editing")
        .marketing("marketing")
        .companies("companies")
        .payout("payout")
        .other("other")
        .build()
      )
      .costs(ContractCostsDocument.sample
        .minimumGuarantee(List(java.math.BigDecimal.TEN).asJava)
        .upfrontLicense(java.math.BigDecimal.ONE)
        .technicalFee(java.math.BigDecimal.TEN)
        .recoupable(true)
        .build()
      )
      .build()

    val contract = DocumentToContractConverter convert document

    contract.id shouldBe ContractId("contract-id")
    contract.name shouldBe "channel name"
    contract.contractDates should contain(ContractDates(
      start = Some(LocalDate ofYearDay(2012, 300)),
      end = Some(LocalDate ofYearDay(2016, 310))
    ))
    contract.contractIsRolling should contain (true)
    contract.daysBeforeTerminationWarning should contain(60)
    contract.yearsForMaximumLicense should contain(30)
    contract.daysForSellOffPeriod should contain(199)
    contract.royaltySplit should contain(ContractRoyaltySplit(
      download = Some(20F),
      streaming = Some(0.9F)
    ))
    contract.minimumPriceDescription should contain ("minimum")
    contract.remittanceCurrency should contain (Currency.getInstance("GBP"))
    contract.restrictions should contain(ContractRestrictions(
      clientFacing = Some(List("client facing")),
      territory = Some("territory"),
      licensing = Some("licensing"),
      editing = Some("editing"),
      marketing = Some("marketing"),
      companies = Some("companies"),
      payout = Some("payout"),
      other = Some("other")
    ))
    contract.costs shouldBe ContractCosts(
      minimumGuarantee = List(java.math.BigDecimal.TEN),
      upfrontLicense = Some(java.math.BigDecimal.ONE),
      technicalFee = Some(java.math.BigDecimal.TEN),
      recoupable = Some(true)
    )
  }

  it should "convert an as-null-as-possible document" in {
    val document = ContractDocument.sample
      .id("contract-id")
      .name("channel name")
      .contractDocumentLink(null)
      .contractDates(null)
      .daysBeforeTerminationWarning(null)
      .yearsForMaximumLicense(null)
      .daysForSellOffPeriod(null)
      .royaltySplit(null)
      .minimumPriceDescription(null)
      .remittanceCurrency(null)
      .restrictions(null)
      .costs(ContractCostsDocument.sample
        .minimumGuarantee(null)
        .upfrontLicense(null)
        .technicalFee(null)
        .recoupable(null)
        .build()
      )
      .build()

    val contract = DocumentToContractConverter convert document

    contract.id shouldBe ContractId("contract-id")
    contract.name shouldBe "channel name"
    contract.contractDates shouldBe None
    contract.daysBeforeTerminationWarning shouldBe None
    contract.yearsForMaximumLicense shouldBe None
    contract.daysForSellOffPeriod shouldBe None
    contract.royaltySplit shouldBe None
    contract.minimumPriceDescription shouldBe None
    contract.remittanceCurrency shouldBe None
    contract.restrictions shouldBe None

    val costs = contract.costs

    costs.minimumGuarantee shouldBe Nil
    costs.upfrontLicense shouldBe None
    costs.technicalFee shouldBe None
    costs.recoupable shouldBe None
  }
}
