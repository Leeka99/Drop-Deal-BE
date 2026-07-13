package com.dropdeal.api.product;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAll(ProductType type) {
        var products = type == null
                ? productRepository.findAllByOrderByIdAsc()
                : productRepository.findByTypeOrderByIdAsc(type);
        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse findById(long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    private ProductResponse toResponse(ProductEntity product) {
        return new ProductResponse(
                product.getId(), product.getName(), product.getSellerName(), product.getDescription(),
                product.getType(), product.getStatus(), product.getOriginalPrice(), product.getStartPrice(),
                product.getCurrentPrice(), product.getMinPrice(), product.getMaxDiscountRate(),
                product.getDiscountStepParticipants(), product.getDiscountStepAmount(), product.getMinParticipants(),
                product.getCurrentParticipants(), product.getMaxParticipants(), product.getRemainingStock(),
                product.getShippingFee(), product.getEndAt(), product.isCouponEvent(), product.getCouponRate(),
                product.getVisual(), product.getIcon(), product.getRating(), product.getReviewCount(),
                toGiveaway(product)
        );
    }

    private GiveawayPolicyResponse toGiveaway(ProductEntity product) {
        if (product.getGiveawayReason() == null) {
            return null;
        }

        var methods = product.getGiveawayFulfillmentMethods() == null
                ? List.<FulfillmentMethod>of()
                : Arrays.stream(product.getGiveawayFulfillmentMethods().split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(FulfillmentMethod::valueOf)
                .toList();
        var pickup = product.getPickupStoreName() == null
                ? null
                : new PickupResponse(
                        product.getPickupStoreName(),
                        product.getPickupAddress(),
                        product.getPickupInstructions(),
                        product.getPickupDeposit() == null ? 0 : product.getPickupDeposit()
                );

        return new GiveawayPolicyResponse(
                product.getGiveawayReason(),
                Boolean.TRUE.equals(product.getGiveawayPromotionalPurpose()),
                methods,
                product.getGiveawayShippingFee(),
                pickup
        );
    }
}
