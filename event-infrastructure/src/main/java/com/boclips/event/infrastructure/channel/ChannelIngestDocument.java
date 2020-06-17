package com.boclips.event.infrastructure.channel;

import lombok.*;

import java.util.Collections;
import java.util.Set;

import static com.boclips.event.infrastructure.channel.DistributionMethodDocument.STREAM;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelIngestDocument {
    @NonNull
    private String type;
    private String deliveryFrequency;
    private Set<DistributionMethodDocument> distributionMethods;

    public static ChannelIngestDocumentBuilder sample() {
        return ChannelIngestDocument.builder()
                .type("MRSS")
                .deliveryFrequency("P1D")
                .distributionMethods(Collections.singleton(STREAM));
    }
}
