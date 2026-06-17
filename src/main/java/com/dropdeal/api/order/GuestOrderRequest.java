package com.dropdeal.api.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record GuestOrderRequest(
        String id,
        @NotBlank String name,
        @NotBlank String phone,
        @NotBlank String productName,
        @PositiveOrZero int paid,
        String state
) {
}
