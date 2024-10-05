package school.faang.user_service.dto.promotion;

import lombok.Getter;

@Getter
public enum PromotionType {
    BASIC( 10, 100, 1),
    PREMIUM(40, 500, 2);

    private final long price;
    private final int showCount;
    private final int priority;

    PromotionType(long price, int showCount, int priority) {
        this.price = price;
        this.showCount = showCount;
        this.priority = priority;
    }
}
