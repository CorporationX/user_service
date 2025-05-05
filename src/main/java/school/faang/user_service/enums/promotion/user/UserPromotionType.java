package school.faang.user_service.enums.promotion.user;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum UserPromotionType {
    TEN_PERCENT_OF_USERS(10, new BigDecimal("10.00")),
    TWENTY_FIVE_PERCENT_OF_USERS(25, new BigDecimal("20.00")),
    FORTY_PERCENT_OF_USERS(40, new BigDecimal("30.00")),
    FIFTY_FIVE_PERCENT_OF_USERS(55, new BigDecimal("40.00")),
    SEVENTY_PERCENT_OF_USERS(70, new BigDecimal("50.00"));

    private final int userPercentage;
    private final BigDecimal basePrice;

    UserPromotionType(int userPercentage, BigDecimal basePrice) {
        this.userPercentage = userPercentage;
        this.basePrice = basePrice;
    }

    public static UserPromotionType fromUserPercentage(int userPercentage) {
        for (UserPromotionType type : values()) {
            if (type.getUserPercentage() == userPercentage) {
                return type;
            }
        }
        throw new IllegalArgumentException("No user promotion type for user percentage: " + userPercentage);
    }
}
