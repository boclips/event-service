package com.boclips.event.aggregator.presentation.assemblers

import com.boclips.event.aggregator.domain.model.ContractLegalRestriction
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.ContractFactory
import com.boclips.event.aggregator.testsupport.testfactories.ContractFactory.createContractRestrictions

class ContractTableRowAssemblerTest extends IntegrationTest {
  it should "bring contracts with no legal restrictions" in sparkTest { implicit spark =>

    val contracts = rdd(
        ContractFactory.createEmptyContract()
    )

    val restrictions = rdd(
      ContractLegalRestriction(id = "id-1", text = "No rules allowed"),
    )


    val contractsWithRelatedData = ContractTableRowAssembler.assembleContractsWithRelatedData(
      contracts = contracts,
      contractLegalRestrictions = restrictions).collect().toList.sortBy(_.contract.id.value)

    contractsWithRelatedData should have size 1

  }

  it should "handle contracts & legal restrictions" in sparkTest { implicit spark =>

    val contracts = rdd(
      ContractFactory.createFullContract(restrictions =
        createContractRestrictions(clientFacing = Some(List("id-1","id-2")))
      )
    )

    val restrictions = rdd(
      ContractLegalRestriction(id = "id-1", text = "No rules allowed"),
      ContractLegalRestriction(id = "id-2", text = "Playback only"),
      ContractLegalRestriction(id = "id-3", text = "Unused Restriction"),
    )


    val contractsWithRelatedData = ContractTableRowAssembler.assembleContractsWithRelatedData(
      contracts = contracts,
      contractLegalRestrictions = restrictions).collect().toList.sortBy(_.contract.id.value)


    contractsWithRelatedData should have size 1
    contractsWithRelatedData.head.clientFacingRestrictions.map(_.id).toList should have size 2
    contractsWithRelatedData.head.clientFacingRestrictions.map(_.id).toList should contain("id-1")
    contractsWithRelatedData.head.clientFacingRestrictions.map(_.id).toList should contain("id-2")

  }
}
