package com.boclips.event.infrastructure.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractCostsDocument {
    private List<BigDecimal> minimumGuarantee;
    private BigDecimal upfrontLicense;
    private BigDecimal technicalFee;
    private Boolean recoupable;

    public static ContractCostsDocumentBuilder sample() {
        return ContractCostsDocument.builder()
                .minimumGuarantee(Collections.singletonList(BigDecimal.TEN))
                .upfrontLicense(BigDecimal.ONE)
                .technicalFee(BigDecimal.ONE)
                .recoupable(true);
    }
}
