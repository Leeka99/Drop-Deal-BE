package com.dropdeal.api.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.dropdeal.api.product.ProductResponse;

public record MemberOrderResponse(
        String id,
        ProductResponse product,
        int paid,
        @JsonProperty("final")
        int finalAmount,
        String state,
        int refund
) {
}
