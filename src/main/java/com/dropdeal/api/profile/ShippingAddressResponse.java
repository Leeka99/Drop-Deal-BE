package com.dropdeal.api.profile;

public record ShippingAddressResponse(
        String recipientName,
        String phone,
        String postalCode,
        String address,
        String detailAddress,
        String deliveryMemo
) {
}
