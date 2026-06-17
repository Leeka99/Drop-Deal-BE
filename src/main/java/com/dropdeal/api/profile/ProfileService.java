package com.dropdeal.api.profile;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProfileService {

    private final AtomicReference<ShippingAddressResponse> defaultShippingAddress = new AtomicReference<>();

    public ShippingAddressResponse getDefaultShippingAddress() {
        return defaultShippingAddress.get();
    }

    public ShippingAddressResponse saveDefaultShippingAddress(ShippingAddressRequest request) {
        var response = new ShippingAddressResponse(
                request.recipientName(),
                request.phone(),
                request.postalCode(),
                request.address(),
                request.detailAddress(),
                request.deliveryMemo()
        );
        defaultShippingAddress.set(response);
        return response;
    }
}
