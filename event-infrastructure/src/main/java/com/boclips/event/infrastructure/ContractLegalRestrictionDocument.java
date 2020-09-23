package com.boclips.event.infrastructure;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractLegalRestrictionDocument {
    private String id;
    private String text;
    public static ContractLegalRestrictionDocumentBuilder sample() {
        return ContractLegalRestrictionDocument.builder()
                .id("id-42")
                .text("fun allowed");
    }
}
