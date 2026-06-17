package com.dropdeal.api.settlement;

public record SettlementOrderResponse(
        String id,
        String productName,
        int finalAmount,
        int refundAmount,
        int platformFee,
        int pgFee,
        int sellerDiscountShare,
        int settlementAmount
) {
}
