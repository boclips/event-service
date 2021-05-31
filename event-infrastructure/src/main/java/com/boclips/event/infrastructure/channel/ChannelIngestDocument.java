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
    private Set<DistributionMethodDocument> distributionMethods;

    public static ChannelIngestDocumentBuilder sample() {
        return ChannelIngestDocument.builder()
                .type("MRSS")
                .distributionMethods(Collections.singleton(STREAM));
    }
}
