package school.faang.user_service.entity.promotion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.exception.IllegalEntityException;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum PromotionalPlan {
    BASIC(10_000, "Local", 30, 100),
    STANDARD(50_000, "Regional", 90, 500),
    ADVANCED(100_000, "National", 200, 1000),
    PREMIUM(250_000, "International", 360, 2500);

    private final int impressions;
    private final String audienceReach;
    private final int durationInDays;
    private final int cost;

    public static PromotionalPlan getFromName(String promotionalPlanName) {
        return Arrays.stream(PromotionalPlan.values())
            .filter(promotionalPlan -> promotionalPlan.name().equals(promotionalPlanName.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new IllegalEntityException(String.format("Promotional plan with name %s does not exist.", promotionalPlanName)));
    }
}
