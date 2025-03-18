package school.faang.user_service.model.promotion.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum EventPromotionType {
    TEN_PERCENT_OF_USERS(10, new BigDecimal("30.00")),
    TWENTY_FIVE_PERCENT_OF_USERS(25, new BigDecimal("40.00")),
    FORTY_PERCENT_OF_USERS(40, new BigDecimal("50.00")),
    FIFTY_PERCENT_OF_USERS(50, new BigDecimal("60.00")),
    SIXTY_PERCENT_OF_USERS(60, new BigDecimal("70.00"));

    private final int userPercentage;
    private final BigDecimal basePrice;

    EventPromotionType(int userPercentage, BigDecimal basePrice) {
        this.userPercentage = userPercentage;
        this.basePrice = basePrice;
    }

    public static EventPromotionType fromUserPercentage(int userPercentage) {
        for (EventPromotionType type : values()) {
            if (type.getUserPercentage() == userPercentage) {
                return type;
            }
        }
        throw new IllegalArgumentException("No event promotion type for user percentage: " + userPercentage);
    }
}
