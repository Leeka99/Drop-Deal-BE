package com.dropdeal.api.product;

import java.util.List;

public record GiveawayPolicyResponse(
        String reason,
        boolean promotionalPurpose,
        List<FulfillmentMethod> fulfillmentMethods,
        Integer shippingFee,
        PickupResponse pickup
) {
}
