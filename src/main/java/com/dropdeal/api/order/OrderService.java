package com.dropdeal.api.order;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class OrderService {

    private static final String DEFAULT_ORDER_STATE = "공동구매 진행 중";
    private static final String CANCELED_ORDER_STATE = "주문 취소 및 환불 예정";

    private final List<GuestOrderResponse> guestOrders = new CopyOnWriteArrayList<>();
    private final List<MemberOrderResponse> memberOrders = new ArrayList<>();

    public GuestOrderResponse createGuestOrder(GuestOrderRequest request) {
        var order = new GuestOrderResponse(
                request.id() == null || request.id().isBlank() ? generateOrderId() : request.id(),
                request.name(),
                request.phone(),
                request.productName(),
                request.paid(),
                request.state() == null || request.state().isBlank() ? DEFAULT_ORDER_STATE : request.state()
        );
        guestOrders.addFirst(order);
        return order;
    }

    public String issueGuestOrderToken(GuestOrderVerifyRequest request) {
        var payload = normalizeName(request.name()) + "|" + normalizePhone(request.phone());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    public List<GuestOrderResponse> findGuestOrders(String token) {
        if (token == null || token.isBlank()) {
            return List.of();
        }
        var identity = parseToken(token);
        if (identity == null) {
            return List.of();
        }
        return guestOrders.stream()
                .filter(order -> normalizeName(order.name()).equals(identity.name())
                        && normalizePhone(order.phone()).equals(identity.phone()))
                .toList();
    }

    public GuestOrderResponse cancelGuestOrder(String orderId) {
        for (int index = 0; index < guestOrders.size(); index++) {
            var order = guestOrders.get(index);
            if (order.id().equals(orderId)) {
                var canceled = new GuestOrderResponse(order.id(), order.name(), order.phone(), order.productName(), order.paid(), CANCELED_ORDER_STATE);
                guestOrders.set(index, canceled);
                return canceled;
            }
        }
        throw new IllegalArgumentException("Guest order not found: " + orderId);
    }

    public List<MemberOrderResponse> findMemberOrders() {
        return memberOrders;
    }

    public MemberOrderResponse cancelMemberOrder(String orderId) {
        for (int index = 0; index < memberOrders.size(); index++) {
            var order = memberOrders.get(index);
            if (order.id().equals(orderId)) {
                var canceled = new MemberOrderResponse(order.id(), order.product(), order.paid(), order.finalAmount(), CANCELED_ORDER_STATE, order.refund());
                memberOrders.set(index, canceled);
                return canceled;
            }
        }
        throw new IllegalArgumentException("Member order not found: " + orderId);
    }

    private String generateOrderId() {
        var date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        var suffix = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "DD-" + date + "-" + suffix;
    }

    private Identity parseToken(String token) {
        try {
            var decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            var parts = decoded.split("\\|", 2);
            if (parts.length != 2) {
                return null;
            }
            return new Identity(parts[0], parts[1]);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private String normalizeName(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizePhone(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private record Identity(String name, String phone) {
    }
}
