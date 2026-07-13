package com.dropdeal.api.settlement;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SettlementService {

    private final List<SettlementResponse> settlements = List.of();

    // TODO: 실제 정산 데이터 연동 후 제거
    /* 임시 데이터
    private final List<SettlementResponse> settlements = List.of(
            new SettlementResponse(
                    260601,
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2026, 6, 7),
                    2840000,
                    318000,
                    284000,
                    85200,
                    40000,
                    0,
                    2430800,
                    SettlementStatus.PAID,
                    null,
                    LocalDate.of(2026, 6, 12),
                    null,
                    List.of(
                            new SettlementOrderResponse("DD-260601-1042", "제주 못난이 감귤 5kg", 1200000, 140000, 120000, 36000, 0, 1044000),
                            new SettlementOrderResponse("DD-260603-1188", "스페셜티 원두 1kg", 990000, 98000, 99000, 29700, 0, 861300)
                    )
            ),
            new SettlementResponse(
                    260608,
                    LocalDate.of(2026, 6, 8),
                    LocalDate.of(2026, 6, 14),
                    3610000,
                    426000,
                    361000,
                    108300,
                    76000,
                    0,
                    3064700,
                    SettlementStatus.READY,
                    LocalDate.of(2026, 6, 19),
                    null,
                    null,
                    List.of(
                            new SettlementOrderResponse("DD-260609-1401", "유통기한 임박 그래놀라 6종 세트", 1520000, 210000, 152000, 45600, 40000, 1282400),
                            new SettlementOrderResponse("DD-260611-1517", "못난이 사과 혼합 박스", 830000, 90000, 83000, 24900, 0, 722100)
                    )
            ),
            new SettlementResponse(
                    260615,
                    LocalDate.of(2026, 6, 15),
                    LocalDate.of(2026, 6, 21),
                    1980000,
                    184000,
                    198000,
                    59400,
                    0,
                    0,
                    1722600,
                    SettlementStatus.ON_HOLD,
                    null,
                    null,
                    "배송 완료 확인이 필요한 주문이 있습니다.",
                    List.of(
                            new SettlementOrderResponse("DD-260615-1702", "제주 못난이 감귤 5kg", 1080000, 104000, 108000, 32400, 0, 939600)
                    )
            )
    );
    */

    public List<SettlementResponse> findAll() {
        return settlements;
    }

    public SettlementResponse findById(long id) {
        return settlements.stream()
                .filter(settlement -> settlement.id() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found: " + id));
    }
}
