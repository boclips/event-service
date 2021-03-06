package com.boclips.event.infrastructure.order;

import lombok.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class OrderDocument {
    @NonNull
    private String id;
    private String legacyOrderId;
    @NonNull
    private String status;
    @NonNull
    private Date createdAt;
    @NonNull
    private Date updatedAt;

    private Date deliveredAt;
    @NonNull
    private String customerOrganisationName;
    @NonNull
    private List<OrderItemDocument> items;
    @NonNull
    private OrderUserDocument requestingUser;

    private OrderUserDocument authorisingUser;

    @NonNull
    private String orderSource;

    private String isbnOrProductNumber;

    private String currency;

    private BigDecimal fxRateToGbp;

    public static OrderDocumentBuilder sample() {
        return OrderDocument.builder()
                .id("order-id")
                .legacyOrderId("leg-order-id")
                .status("my status")
                .createdAt(new Date())
                .updatedAt(new Date())
                .deliveredAt(new Date())
                .customerOrganisationName("my organization")
                .authorisingUser(OrderUserDocument.sample().build())
                .requestingUser(OrderUserDocument.sample().build())
                .orderSource("MANUAL")
                .isbnOrProductNumber("flux-cd")
                .currency(Currency.getInstance("USD").getCurrencyCode())
                .fxRateToGbp(BigDecimal.TEN)
                .items(Collections.singletonList(
                        OrderItemDocument.sample().build()
                ));
    }
}
