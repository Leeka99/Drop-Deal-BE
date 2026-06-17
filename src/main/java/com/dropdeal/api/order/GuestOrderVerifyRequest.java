package com.dropdeal.api.order;

import jakarta.validation.constraints.NotBlank;

public record GuestOrderVerifyRequest(
        @NotBlank String name,
        @NotBlank String phone
) {
}
