package com.dropdeal.api.settlement;

import java.time.LocalDate;
import java.util.List;

public record SettlementResponse(
        long id,
        LocalDate periodStart,
        LocalDate periodEnd,
        int grossAmount,
        int refundAmount,
        int platformFee,
        int pgFee,
        int sellerDiscountShare,
        int additionalFee,
        int settlementAmount,
        SettlementStatus status,
        LocalDate scheduledAt,
        LocalDate paidAt,
        String holdReason,
        List<SettlementOrderResponse> orders
) {
}
