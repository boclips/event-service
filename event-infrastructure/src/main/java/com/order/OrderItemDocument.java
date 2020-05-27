package com.order;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDocument {
    @NonNull
    private String videoId;
    @NonNull
    private String priceGbp;

    public static OrderItemDocumentBuilder sample() {
        return OrderItemDocument.builder()
                .videoId("video-id")
                .priceGbp("123123");
    }
}
