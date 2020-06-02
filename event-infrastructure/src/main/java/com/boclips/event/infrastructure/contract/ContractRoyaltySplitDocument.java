package com.boclips.event.infrastructure.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractRoyaltySplitDocument {
    private Float download;
    private Float streaming;

    public static ContractRoyaltySplitDocumentBuilder sample() {
        return ContractRoyaltySplitDocument.builder()
                .download(60F)
                .streaming(0.1F);
    }
}
