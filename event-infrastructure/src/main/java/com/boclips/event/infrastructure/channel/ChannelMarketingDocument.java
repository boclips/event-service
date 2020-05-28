package com.boclips.event.infrastructure.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMarketingDocument {
    private String status;
    private String oneLineIntro;
    private List<String> logos;
    private String showreel;
    private List<String> sampleVideos;

    public static ChannelMarketingDocumentBuilder sample() {
        return ChannelMarketingDocument.builder()
                .status("Status")
                .oneLineIntro("One line intro")
                .logos(Collections.singletonList("http://logo.com/it.mp4"))
                .showreel("https://showreel.com/it.png")
                .sampleVideos(Collections.singletonList("https://samplevideos.com/it.mov"));
    }
}
