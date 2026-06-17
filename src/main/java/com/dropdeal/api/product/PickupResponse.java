package com.dropdeal.api.product;

public record PickupResponse(
        String storeName,
        String address,
        String instructions,
        int deposit
) {
}
