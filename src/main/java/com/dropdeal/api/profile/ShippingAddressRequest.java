package com.dropdeal.api.profile;

import jakarta.validation.constraints.NotBlank;

public record ShippingAddressRequest(
        @NotBlank String recipientName,
        @NotBlank String phone,
        @NotBlank String postalCode,
        @NotBlank String address,
        String detailAddress,
        String deliveryMemo
) {
}
