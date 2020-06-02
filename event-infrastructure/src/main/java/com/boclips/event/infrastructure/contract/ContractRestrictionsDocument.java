package com.boclips.event.infrastructure.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractRestrictionsDocument {
    private List<String> clientFacing;
    private String territory;
    private String licensing;
    private String editing;
    private String marketing;
    private String companies;
    private String payout;
    private String other;

    public static ContractRestrictionsDocumentBuilder sample() {
        return ContractRestrictionsDocument.builder()
                .clientFacing(Collections.singletonList("client-facing"))
                .territory("territory")
                .licensing("licensing")
                .editing("editing")
                .marketing("marketing")
                .companies("companies")
                .payout("payout")
                .other("other");
    }
}
