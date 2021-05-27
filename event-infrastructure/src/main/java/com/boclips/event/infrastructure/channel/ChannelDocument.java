package com.boclips.event.infrastructure.channel;

import lombok.*;

import java.util.Collections;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDocument {
    @NonNull
    private String id;
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
    @NonNull
    private Set<CategoryWithAncestorsDocument> categories;

    public static ChannelDocumentBuilder sample() {
        return ChannelDocument.builder()
                .id("channel-id")
                .name("channel name")
                .details(ChannelDetailsDocument.sample().build())
                .ingest(ChannelIngestDocument.sample().build())
                .pedagogy(ChannelPedagogyDocument.sample().build())
                .marketing(ChannelMarketingDocument.sample().build())
                .categories(Collections.singleton(CategoryWithAncestorsDocument.sample().build()));
    }
}
