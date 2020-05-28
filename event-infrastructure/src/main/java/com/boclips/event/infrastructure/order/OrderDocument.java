package com.boclips.event.infrastructure.order;

import lombok.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class OrderDocument {
    @NonNull
    private String _id;
    @NonNull
    private String status;
    @NonNull
    private Date createdAt;
    @NonNull
    private Date updatedAt;
    @NonNull
    private String customerOrganisationName;
    @NonNull
    private List<OrderItemDocument> items;

    public static OrderDocumentBuilder sample() {
        return OrderDocument.builder()
                ._id("order-id")
                .status("my status")
                .createdAt(new Date())
                .updatedAt(new Date())
                .customerOrganisationName("my organization")
                .items(Collections.singletonList(
                        OrderItemDocument.sample().build()
                ));
    }
}