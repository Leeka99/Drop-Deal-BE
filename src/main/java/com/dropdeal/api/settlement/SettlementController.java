package com.dropdeal.api.settlement;

import com.dropdeal.api.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seller/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping
    public ApiResponse<List<SettlementResponse>> getSettlements() {
        return ApiResponse.ok(settlementService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<SettlementResponse> getSettlement(@PathVariable long id) {
        return ApiResponse.ok(settlementService.findById(id));
    }
}
