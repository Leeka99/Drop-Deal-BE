package com.dropdeal.api.product;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final List<ProductResponse> products = List.of(
            new ProductResponse(1, "제주 못난이 감귤 5kg", "귤빛농원", "제주 산지에서 바로 보내는 당도 높은 감귤입니다.",
                    ProductType.NORMAL, ProductStatus.OPEN, 32000, 30400, 29100, 24000, 25,
                    7, 1300, 15, 27, 50, 23, 3000, LocalDateTime.now().plusDays(3),
                    false, null, "visual-citrus", "citrus", 4.8, 128, null),
            new ProductResponse(2, "스페셜티 원두 1kg", "하루로스터스", "매일 아침 로스팅하는 고소한 원두입니다.",
                    ProductType.NORMAL, ProductStatus.OPEN, 42000, 39900, 37500, 31500, 25,
                    9, 1200, 20, 41, 80, 39, 3000, LocalDateTime.now().plusDays(4),
                    false, null, "visual-coffee", "coffee", 4.9, 84, null),
            new ProductResponse(3, "유통기한 임박 그래놀라 6종 세트", "오늘의곳간", "품질은 그대로, 유통기한이 가까운 상품을 합리적인 가격으로 제공합니다.",
                    ProductType.CLEARANCE, ProductStatus.OPEN, 36000, 18000, 18000, 10800, 70,
                    49, 7200, 14, 46, 70, 24, 3000, LocalDateTime.now().plusDays(2),
                    false, null, "visual-granola", "granola", 4.7, 95, null),
            new ProductResponse(4, "못난이 사과 혼합 박스", "들녘농장", "모양은 조금 달라도 맛과 신선함은 그대로인 사과입니다.",
                    ProductType.CLEARANCE, ProductStatus.OPEN, 30000, 15000, 15000, 9000, 70,
                    56, 6000, 16, 52, 80, 28, 3000, LocalDateTime.now().plusDays(1),
                    false, null, "visual-apple", "apple", 4.5, 78, null),
            new ProductResponse(5, "카페 머그컵", "동네카페 그린스푼", "재고 정리 목적의 무료 나눔 상품입니다. 배송 또는 매장 픽업이 가능합니다.",
                    ProductType.FREE_GIVEAWAY, ProductStatus.OPEN, 0, 0, 0, 0, 100,
                    0, 0, 1, 34, 80, 46, null, LocalDateTime.now().plusDays(5),
                    false, null, "visual-coffee", "mug", 0, 0,
                    new GiveawayPolicyResponse("재고 정리", false, List.of(FulfillmentMethod.SHIPPING, FulfillmentMethod.PICKUP), 3000,
                            new PickupResponse("동네카페 그린스푼", "서울시 마포구 월드컵로 10", "오후 1시부터 6시 사이 방문", 2000)))
    );

    public List<ProductResponse> findAll(ProductType type) {
        if (type == null) {
            return products;
        }
        return products.stream()
                .filter(product -> product.type() == type)
                .toList();
    }

    public ProductResponse findById(long id) {
        return products.stream()
                .filter(product -> product.id() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }
}
