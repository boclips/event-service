package com.boclips.event.infrastructure.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDatesDocument {
    private LocalDate start;
    private LocalDate end;

    public static ContractDatesDocumentBuilder sample() {
        return ContractDatesDocument.builder()
                .start(LocalDate.ofYearDay(2008, 300))
                .end(LocalDate.ofYearDay(20010, 305));
    }
}
