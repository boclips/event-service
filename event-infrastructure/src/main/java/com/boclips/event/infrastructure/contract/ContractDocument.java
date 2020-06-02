package com.boclips.event.infrastructure.contract;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDocument {
    @NonNull
    private String id;

    @NonNull
    private String channelName;

    private String contractDocumentLink;
    private Boolean contractIsRolling;
    private ContractDatesDocument contractDates;
    private Integer daysBeforeTerminationWarning;
    private Integer yearsForMaximumLicense;
    private Integer daysForSellOffPeriod;
    private ContractRoyaltySplitDocument royaltySplit;
    private String minimumPriceDescription;
    private String remittanceCurrency;
    private ContractRestrictionsDocument restrictions;
    private ContractCostsDocument costs;

    public static ContractDocumentBuilder sample() {
        return ContractDocument.builder()
                .id("contract-id")
                .channelName("channel name")
                .contractDocumentLink("http://doc.com/it.pdf")
                .contractIsRolling(true)
                .contractDates(ContractDatesDocument.sample().build())
                .daysBeforeTerminationWarning(60)
                .yearsForMaximumLicense(120)
                .daysForSellOffPeriod(34)
                .royaltySplit(ContractRoyaltySplitDocument.sample().build())
                .minimumPriceDescription("minimum price description")
                .remittanceCurrency("USD")
                .restrictions(ContractRestrictionsDocument.sample().build())
                .costs(ContractCostsDocument.sample().build());
    }
}
