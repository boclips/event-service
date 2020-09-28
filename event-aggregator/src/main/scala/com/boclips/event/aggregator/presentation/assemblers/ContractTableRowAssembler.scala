package com.boclips.event.aggregator.presentation.assemblers

import com.boclips.event.aggregator.domain.model.ContractLegalRestriction
import com.boclips.event.aggregator.domain.model.contentpartners.{Contract, ContractId}
import com.boclips.event.aggregator.presentation.model.ContractTableRow
import org.apache.spark.rdd.RDD

object ContractTableRowAssembler {

  def assembleContractsWithRelatedData(contracts: RDD[Contract],
                                       contractLegalRestrictions: RDD[ContractLegalRestriction],
                                      ): RDD[ContractTableRow] = {

    val restrictionsMap = contractLegalRestrictions.keyBy(_.id) collectAsMap()

    val restrictionByContractId: RDD[(ContractId, List[ContractLegalRestriction])] = contracts.flatMap(
      contract => contract.restrictions.flatMap(
        _.clientFacing.map {
          case restriction => (contract.id, restriction.map(restrictionsMap(_)))
        }
      ))
      .keyBy(_._1)
      .values

    contracts.keyBy(_.id)
      .leftOuterJoin(restrictionByContractId)
      .values
      .map {
        case (contract, legalRestrictions) =>
          ContractTableRow(contract,legalRestrictions)
      }
    }
}
