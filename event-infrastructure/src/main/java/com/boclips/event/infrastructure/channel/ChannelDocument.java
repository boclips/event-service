package com.boclips.event.infrastructure.channel;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDocument {
    @NonNull
    private String _id;
    @NonNull
    private String name;
    @NonNull
    private ChannelDetailsDocument details;
    @NonNull
    private ChannelIngestDocument ingest;
    @NonNull
    private ChannelPedagogyDocument pedagogy;
    @NonNull
    private ChannelMarketingDocument marketing;

    public static ChannelDocumentBuilder sample() {
        return ChannelDocument.builder()
                ._id("channel-id")
                .name("channel name")
                .details(ChannelDetailsDocument.sample().build())
                .ingest(ChannelIngestDocument.sample().build())
                .pedagogy(ChannelPedagogyDocument.sample().build())
                .marketing(ChannelMarketingDocument.sample().build());
    }
}
