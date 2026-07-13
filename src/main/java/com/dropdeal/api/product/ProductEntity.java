package com.dropdeal.api.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "seller_name", nullable = false)
    private String sellerName;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus status;

    @Column(name = "original_price", nullable = false)
    private int originalPrice;

    @Column(name = "start_price", nullable = false)
    private int startPrice;

    @Column(name = "current_price", nullable = false)
    private int currentPrice;

    @Column(name = "min_price", nullable = false)
    private int minPrice;

    @Column(name = "max_discount_rate", nullable = false)
    private int maxDiscountRate;

    @Column(name = "discount_step_participants", nullable = false)
    private int discountStepParticipants;

    @Column(name = "discount_step_amount", nullable = false)
    private int discountStepAmount;

    @Column(name = "min_participants", nullable = false)
    private int minParticipants;

    @Column(name = "current_participants", nullable = false)
    private int currentParticipants;

    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Column(name = "remaining_stock", nullable = false)
    private int remainingStock;

    @Column(name = "shipping_fee")
    private Integer shippingFee;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "coupon_event", nullable = false)
    private boolean couponEvent;

    @Column(name = "coupon_rate")
    private Integer couponRate;

    private String visual;
    private String icon;

    @Column(nullable = false)
    private double rating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "giveaway_reason", length = 500)
    private String giveawayReason;

    @Column(name = "giveaway_promotional_purpose")
    private Boolean giveawayPromotionalPurpose;

    @Column(name = "giveaway_fulfillment_methods", length = 100)
    private String giveawayFulfillmentMethods;

    @Column(name = "giveaway_shipping_fee")
    private Integer giveawayShippingFee;

    @Column(name = "pickup_store_name")
    private String pickupStoreName;

    @Column(name = "pickup_address", length = 500)
    private String pickupAddress;

    @Column(name = "pickup_instructions", length = 1000)
    private String pickupInstructions;

    @Column(name = "pickup_deposit")
    private Integer pickupDeposit;

    protected ProductEntity() {
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSellerName() { return sellerName; }
    public String getDescription() { return description; }
    public ProductType getType() { return type; }
    public ProductStatus getStatus() { return status; }
    public int getOriginalPrice() { return originalPrice; }
    public int getStartPrice() { return startPrice; }
    public int getCurrentPrice() { return currentPrice; }
    public int getMinPrice() { return minPrice; }
    public int getMaxDiscountRate() { return maxDiscountRate; }
    public int getDiscountStepParticipants() { return discountStepParticipants; }
    public int getDiscountStepAmount() { return discountStepAmount; }
    public int getMinParticipants() { return minParticipants; }
    public int getCurrentParticipants() { return currentParticipants; }
    public int getMaxParticipants() { return maxParticipants; }
    public int getRemainingStock() { return remainingStock; }
    public Integer getShippingFee() { return shippingFee; }
    public LocalDateTime getEndAt() { return endAt; }
    public boolean isCouponEvent() { return couponEvent; }
    public Integer getCouponRate() { return couponRate; }
    public String getVisual() { return visual; }
    public String getIcon() { return icon; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public String getGiveawayReason() { return giveawayReason; }
    public Boolean getGiveawayPromotionalPurpose() { return giveawayPromotionalPurpose; }
    public String getGiveawayFulfillmentMethods() { return giveawayFulfillmentMethods; }
    public Integer getGiveawayShippingFee() { return giveawayShippingFee; }
    public String getPickupStoreName() { return pickupStoreName; }
    public String getPickupAddress() { return pickupAddress; }
    public String getPickupInstructions() { return pickupInstructions; }
    public Integer getPickupDeposit() { return pickupDeposit; }
}
