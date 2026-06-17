package com.dropdeal.api.order;

import com.dropdeal.api.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/me/orders")
    public ApiResponse<List<MemberOrderResponse>> getMemberOrders() {
        return ApiResponse.ok(orderService.findMemberOrders());
    }

    @PostMapping("/orders")
    public ApiResponse<GuestOrderResponse> createGuestOrder(@Valid @RequestBody GuestOrderRequest request) {
        return ApiResponse.ok(orderService.createGuestOrder(request));
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ApiResponse<MemberOrderResponse> cancelMemberOrder(
            @PathVariable String orderId,
            @RequestBody(required = false) CancelOrderRequest request
    ) {
        return ApiResponse.ok(orderService.cancelMemberOrder(orderId));
    }

    @PostMapping("/guest-orders/verify")
    public ApiResponse<GuestOrderTokenResponse> verifyGuestOrder(@Valid @RequestBody GuestOrderVerifyRequest request) {
        return ApiResponse.ok(new GuestOrderTokenResponse(orderService.issueGuestOrderToken(request)));
    }

    @GetMapping("/guest-orders")
    public ApiResponse<List<GuestOrderResponse>> getGuestOrders(
            @RequestHeader(name = "Guest-Order-Token", required = false) String token
    ) {
        return ApiResponse.ok(orderService.findGuestOrders(token));
    }

    @PostMapping("/guest-orders/{orderId}/cancel")
    public ApiResponse<GuestOrderResponse> cancelGuestOrder(
            @PathVariable String orderId,
            @RequestHeader(name = "Guest-Order-Token", required = false) String token,
            @RequestBody(required = false) CancelOrderRequest request
    ) {
        return ApiResponse.ok(orderService.cancelGuestOrder(orderId));
    }
}
