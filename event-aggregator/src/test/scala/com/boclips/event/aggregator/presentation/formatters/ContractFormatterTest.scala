package com.boclips.event.aggregator.presentation.formatters

import java.time.LocalDate
import java.util.Currency

import com.boclips.event.aggregator.domain.model.contentpartners.{ContractCosts, ContractDates, ContractRestrictions, ContractRoyaltySplit}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.ContractFactory.{createContractRestriction, createEmptyContract, createFullContract, createFullTableRowContract}
import com.google.gson.JsonNull

import scala.collection.JavaConverters._

class ContractFormatterTest extends Test {
  it should "write full contract" in {
    val contract = createFullContract(
      id = "my-contract-id",
      channelName = "my channel name",
      contractDocumentLink = "http://mysite.com",
      contractIsRolling = false,
      contractDates = ContractDates(
        start = Some(LocalDate.ofYearDay(2008, 1)),
        end = Some(LocalDate.ofYearDay(2021, 360))
      ),
      daysBeforeTerminationWarning = 300,
      yearsForMaximumLicense = 1,
      daysForSellOffPeriod = 101,
      royaltySplit = ContractRoyaltySplit(
        download = Some(18.1F),
        streaming = Some(90F)
      ),
      minimumPriceDescription = "minimum price",
      remittanceCurrency = Currency.getInstance("USD"),
      restrictions = ContractRestrictions(
        clientFacing = Some(List("client-facing")),
        territory = Some("territory"),
        licensing = Some("licensing"),
        editing = Some("editing"),
        marketing = Some("marketing"),
        companies = Some("companies"),
        payout = Some("payout"),
        other = Some("other")
      ),
      costs = ContractCosts(
        minimumGuarantee = List(100, 200, 300),
        upfrontLicense = Some(50),
        technicalFee = Some(88),
        recoupable = Some(true)
      )
    )
    val contractWithRestrictions = createFullTableRowContract(contract,
      List(createContractRestriction(id = "client-facing", text = "see it, say it, sorted")))

    val json = ContractFormatter formatRow contractWithRestrictions

    json.getString("id") shouldBe "my-contract-id"
    json.getString("name") shouldBe "my channel name"

    json.getString("contractDocumentLink") shouldBe "http://mysite.com"
    json.getBool("contractIsRolling") shouldBe false
    json.getString("contractStartDate") shouldBe "2008-01-01"
    json.getString("contractEndDate") shouldBe "2021-12-26"
    json.getInt("daysBeforeTerminationWarning") shouldBe 300
    json.getInt("yearsForMaximumLicense") shouldBe 1
    json.getInt("daysForSellOffPeriod") shouldBe 101
    json.getFloat("downloadRoyaltySplit") shouldBe 18.1F
    json.getFloat("streamingRoyaltySplit") shouldBe 90F
    json.getString("minimumPriceDescription") shouldBe "minimum price"
    json.getString("remittanceCurrency") shouldBe "USD"

    json.getStringList("clientFacingRestrictions") should contain("see it, say it, sorted")
    json.getString("territoryRestrictions") shouldBe "territory"
    json.getString("licensingRestrictions") shouldBe "licensing"
    json.getString("editingRestrictions") shouldBe "editing"
    json.getString("marketingRestrictions") shouldBe "marketing"
    json.getString("companiesRestrictions") shouldBe "companies"
    json.getString("payoutRestrictions") shouldBe "payout"
    json.getString("otherRestrictions") shouldBe "other"

    val minimumGuaranteesJson = json.getAsJsonArray("minimumGuarantee")
    minimumGuaranteesJson.asScala.map(_.getAsJsonObject.getInt("contractYear")) shouldBe List(1, 2, 3)
    minimumGuaranteesJson.asScala.map(_.getAsJsonObject.getBigDecimal("amount")) shouldBe List(100, 200, 300)

    json.getBigDecimal("upfrontLicenseCost") shouldBe 50
    json.getBigDecimal("technicalFee") shouldBe 88
    json.getBool("recoupable") shouldBe true
  }

  it should "write all-none contract" in {
    val contract = createEmptyContract(
      id = "my-contract-id",
      channelName = "my channel name"
    )

    val contractWithRestrictions = createFullTableRowContract(contract, Nil)

    val json = ContractFormatter formatRow contractWithRestrictions

    json.getString("id") shouldBe "my-contract-id"
    json.getString("name") shouldBe "my channel name"

    json.get("contractDocumentLink") shouldBe JsonNull.INSTANCE
    json.get("contractIsRolling") shouldBe JsonNull.INSTANCE
    json.get("contractStartDate") shouldBe JsonNull.INSTANCE
    json.get("contractEndDate") shouldBe JsonNull.INSTANCE
    json.get("daysBeforeTerminationWarning") shouldBe JsonNull.INSTANCE
    json.get("yearsForMaximumLicense") shouldBe JsonNull.INSTANCE
    json.get("daysForSellOffPeriod") shouldBe JsonNull.INSTANCE
    json.get("downloadRoyaltySplit") shouldBe JsonNull.INSTANCE
    json.get("streamingRoyaltySplit") shouldBe JsonNull.INSTANCE
    json.get("minimumPriceDescription") shouldBe JsonNull.INSTANCE
    json.get("remittanceCurrency") shouldBe JsonNull.INSTANCE

    json.get("clientFacingRestrictions") shouldBe null
    json.get("territoryRestrictions") shouldBe null
    json.get("licensingRestrictions") shouldBe null
    json.get("editingRestrictions") shouldBe null
    json.get("marketingRestrictions") shouldBe null
    json.get("companiesRestrictions") shouldBe null
    json.get("payoutRestrictions") shouldBe null
    json.get("otherRestrictions") shouldBe null

    json.getStringList("minimumGuarantee") shouldBe List()
    json.get("upfrontLicenseCost") shouldBe JsonNull.INSTANCE
    json.get("technicalFee") shouldBe JsonNull.INSTANCE
    json.get("recoupable") shouldBe JsonNull.INSTANCE
  }
}
