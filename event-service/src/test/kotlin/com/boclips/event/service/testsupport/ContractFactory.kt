package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.contract.Contract
import com.boclips.eventbus.domain.contract.ContractCosts
import com.boclips.eventbus.domain.contract.ContractDates
import com.boclips.eventbus.domain.contract.ContractId
import com.boclips.eventbus.domain.contract.ContractRestrictions
import com.boclips.eventbus.domain.contract.ContractRoyaltySplit
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.Currency

object ContractFactory {
    fun createContract(
        id: String = "contract-id",
        name: String = "contract-name",
        contractDocument: String = "http://contractdocument.com",
        contractIsRolling: Boolean = true,
        contractDates: ContractDates = createContractDates(),
        daysBeforeTerminationWarning: Int = 30,
        yearsForMaximumLicense: Int = 60,
        daysForSellOffPeriod: Int = 90,
        royaltySplit: ContractRoyaltySplit = createContractRoyaltySplit(),
        minimumPriceDescription: String = "minimum price description",
        remittanceCurrency: Currency = Currency.getInstance("GBP"),
        restrictions: ContractRestrictions = createContractRestrictions(),
        costs: ContractCosts = createContractCosts()
    ): Contract =
        Contract.builder()
            .contractId(ContractId(id))
            .name(name)
            .contractDocument(contractDocument)
            .contractIsRolling(contractIsRolling)
            .contractDates(contractDates)
            .daysBeforeTerminationWarning(daysBeforeTerminationWarning)
            .yearsForMaximumLicense(yearsForMaximumLicense)
            .daysForSellOffPeriod(daysForSellOffPeriod)
            .royaltySplit(royaltySplit)
            .minimumPriceDescription(minimumPriceDescription)
            .remittanceCurrency(remittanceCurrency)
            .restrictions(restrictions)
            .costs(costs)
            .build()

    fun createContractDates(
        start: LocalDate = LocalDate.of(2020, Month.DECEMBER, 31),
        end: LocalDate = LocalDate.of(2022, Month.JANUARY, 1)
    ): ContractDates =
        ContractDates.builder()
            .start(start)
            .end(end)
            .build()

    fun createContractRoyaltySplit(
        download: Float = 50F,
        streaming: Float = 60.3F
    ): ContractRoyaltySplit =
        ContractRoyaltySplit.builder()
            .download(download)
            .streaming(streaming)
            .build()

    fun createContractRestrictions(
        clientFacing: List<String> = listOf("client-facing"),
        territory: String = "territory",
        licensing: String = "licensing",
        editing: String = "editing",
        marketing: String = "marketing",
        companies: String = "companies",
        payout: String = "payout",
        other: String = "other"
    ): ContractRestrictions =
        ContractRestrictions.builder()
            .clientFacing(clientFacing)
            .territory(territory)
            .licensing(licensing)
            .editing(editing)
            .marketing(marketing)
            .companies(companies)
            .payout(payout)
            .other(other)
            .build()

    fun createContractCosts(
        minimumGuarantee: List<BigDecimal> = listOf(BigDecimal.TEN),
        upfrontLicense: BigDecimal = BigDecimal.ONE,
        technicalFee: BigDecimal = BigDecimal.ZERO,
        recoupable: Boolean = true
    ): ContractCosts =
        ContractCosts.builder()
            .minimumGuarantee(minimumGuarantee)
            .upfrontLicense(upfrontLicense)
            .technicalFee(technicalFee)
            .recoupable(recoupable)
            .build()
}