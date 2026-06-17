package com.dropdeal.api.product;

import java.time.LocalDateTime;

public record ProductResponse(
        long id,
        String name,
        String sellerName,
        String description,
        ProductType type,
        ProductStatus status,
        int originalPrice,
        int startPrice,
        int currentPrice,
        int minPrice,
        int maxDiscountRate,
        int discountStepParticipants,
        int discountStepAmount,
        int minParticipants,
        int currentParticipants,
        int maxParticipants,
        int remainingStock,
        Integer shippingFee,
        LocalDateTime endAt,
        boolean couponEvent,
        Integer couponRate,
        String visual,
        String icon,
        double rating,
        int reviewCount,
        GiveawayPolicyResponse giveaway
) {
}
