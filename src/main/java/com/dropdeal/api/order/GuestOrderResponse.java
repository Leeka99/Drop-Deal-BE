package com.dropdeal.api.order;

public record GuestOrderResponse(
        String id,
        String name,
        String phone,
        String productName,
        int paid,
        String state
) {
}
