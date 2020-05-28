package com.boclips.event.infrastructure.channel;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelIngestDocument {
    @NonNull
    private String type;
    private String deliveryFrequency;

    public static ChannelIngestDocumentBuilder sample() {
        return ChannelIngestDocument.builder()
                .type("MRSS")
                .deliveryFrequency("P1D");
    }
}
