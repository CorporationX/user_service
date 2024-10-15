package school.faang.user_service.model.enums;

import lombok.Getter;

@Getter
public enum PromotionType {
    GENERAL(1, 100, 10),
    ADVANCED(2, 500, 50),
    PREMIUM(3, 750, 85);

    private final int priorityLevel;
    private final int numberOfShows;
    private final int price;

    PromotionType(int priorityLevel, int numberOfShows, int price) {
        this.priorityLevel = priorityLevel;
        this.numberOfShows = numberOfShows;
        this.price = price;
    }
}
