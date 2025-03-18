package school.faang.user_service.model.promotion;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum PromotionPriority {
    PRIORITY_ULTRA(10, new BigDecimal("54.0")),
    PRIORITY_PREMIUM(50, new BigDecimal("36.0")),
    PRIORITY_HIGH(100, new BigDecimal("10.0")),
    PRIORITY_MEDIUM(250, new BigDecimal("5.0")),
    PRIORITY_NORMAL(500, new BigDecimal("3.0")),
    PRIORITY_LOW(1000, new BigDecimal("2.0")),
    PRIORITY_MINIMAL(2500, new BigDecimal("1.0"));

    private final int feedRank;
    private final BigDecimal multiplier;

    PromotionPriority(int feedRank, BigDecimal multiplier) {
        this.feedRank = feedRank;
        this.multiplier = multiplier;
    }

    public static PromotionPriority fromFeedRank(int feedRank) {
        for (PromotionPriority priority : values()) {
            if (priority.getFeedRank() == feedRank) {
                return priority;
            }
        }
        throw new IllegalArgumentException("No promotion priority for feed rank: " + feedRank);
    }
}
