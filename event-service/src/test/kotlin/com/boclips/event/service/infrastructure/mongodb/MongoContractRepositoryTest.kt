package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.contract.ContractDocument
import com.boclips.event.service.domain.ContractRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.ContractFactory.createContract
import com.boclips.event.service.testsupport.ContractFactory.createContractCosts
import com.boclips.event.service.testsupport.ContractFactory.createContractDates
import com.boclips.event.service.testsupport.ContractFactory.createContractRestrictions
import com.boclips.event.service.testsupport.ContractFactory.createContractRoyaltySplit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.Currency

class MongoContractRepositoryTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var repository: ContractRepository

    @Test
    fun `save and update a contract with only id and name`() {
        val id = "my-simple-channel"
        repository.save(createContract(id = id, channelName = "My simple contract."))

        val created = getSingleDocument()
        assertThat(created.id).isEqualTo(id)
        assertThat(created.channelName).isEqualTo("My simple contract.")

        repository.save(createContract(id = id, channelName = "My new name"))

        val updated = getSingleDocument()
        assertThat(updated.id).isEqualTo(id)
        assertThat(updated.channelName).isEqualTo("My new name")
    }

    @Test
    fun `save fully-filled-out contract`() {
        repository.save(
            createContract(
                id = "contract-id",
                channelName = "content partner name",
                contractDocument = "http://google.com",
                contractIsRolling = true,
                contractDates = createContractDates(
                    start = LocalDate.of(2010, Month.NOVEMBER, 11),
                    end = LocalDate.of(2012, Month.MAY, 3)
                ),
                daysBeforeTerminationWarning = 12,
                yearsForMaximumLicense = 3,
                daysForSellOffPeriod = 21,
                royaltySplit = createContractRoyaltySplit(
                    download = 20F,
                    streaming = 0.5F
                ),
                minimumPriceDescription = "minimum price",
                remittanceCurrency = Currency.getInstance("GBP"),
                restrictions = createContractRestrictions(
                    clientFacing = listOf("client-facing"),
                    territory = "territory",
                    licensing = "licensing",
                    editing = "editing",
                    marketing = "marketing",
                    companies = "companies",
                    payout = "payout",
                    other = "other"
                ),
                costs = createContractCosts(
                    minimumGuarantee = listOf(BigDecimal.TEN),
                    upfrontLicense = BigDecimal.ONE,
                    technicalFee = BigDecimal.ZERO,
                    recoupable = true
                )
            )
        )

        val document = getSingleDocument()

        assertThat(document.id).isEqualTo("contract-id")
        assertThat(document.channelName).isEqualTo("content partner name")
        assertThat(document.contractDocumentLink).isEqualTo("http://google.com")
        assertThat(document.contractIsRolling).isTrue()
        assertThat(document.contractDates.start).isEqualTo(LocalDate.of(2010, Month.NOVEMBER, 11))
        assertThat(document.contractDates.end).isEqualTo(LocalDate.of(2012, Month.MAY, 3))
        assertThat(document.daysBeforeTerminationWarning).isEqualTo(12)
        assertThat(document.yearsForMaximumLicense).isEqualTo(3)
        assertThat(document.daysForSellOffPeriod).isEqualTo(21)
        assertThat(document.royaltySplit.download).isEqualTo(20F)
        assertThat(document.royaltySplit.streaming).isEqualTo(0.5F)
        assertThat(document.minimumPriceDescription).isEqualTo("minimum price")
        assertThat(document.remittanceCurrency).isEqualTo("GBP")

        val restrictions = document.restrictions

        assertThat(restrictions.clientFacing).containsExactly("client-facing")
        assertThat(restrictions.territory).isEqualTo("territory")
        assertThat(restrictions.licensing).isEqualTo("licensing")
        assertThat(restrictions.editing).isEqualTo("editing")
        assertThat(restrictions.marketing).isEqualTo("marketing")
        assertThat(restrictions.companies).isEqualTo("companies")
        assertThat(restrictions.payout).isEqualTo("payout")
        assertThat(restrictions.other).isEqualTo("other")

        val costs = document.costs

        assertThat(costs.minimumGuarantee).containsExactly(BigDecimal.TEN)
        assertThat(costs.upfrontLicense).isEqualTo(BigDecimal.ONE)
        assertThat(costs.technicalFee).isEqualTo(BigDecimal.ZERO)
        assertThat(costs.recoupable).isEqualTo(true)
    }

    fun getSingleDocument() = document<ContractDocument>(MongoContractRepository.COLLECTION_NAME)
}