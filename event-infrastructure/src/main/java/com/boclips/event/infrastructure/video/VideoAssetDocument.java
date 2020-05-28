package com.boclips.event.infrastructure.video;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoAssetDocument {
    @NonNull
    private String id;
    @NonNull
    private Integer width;
    @NonNull
    private Integer height;
    @NonNull
    private Integer bitrateKbps;
    @NonNull
    private Integer sizeKb;

    public static VideoAssetDocumentBuilder sample() {
        return VideoAssetDocument.builder()
                .id("video-asset-id")
                .width(1920)
                .height(1080)
                .bitrateKbps(85)
                .sizeKb(1000000);
    }
}
