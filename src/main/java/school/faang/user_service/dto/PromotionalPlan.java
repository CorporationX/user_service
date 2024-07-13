package school.faang.user_service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.promotion.AudienceReach;
import school.faang.user_service.exception.IllegalEntityException;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum PromotionalPlan {
    BASIC(10_000, AudienceReach.LOCAL, 100),
    STANDARD(50_000, AudienceReach.REGIONAL, 500),
    ADVANCED(100_000, AudienceReach.NATIONAL, 1000),
    PREMIUM(250_000, AudienceReach.INTERNATIONAL, 2500);

    private final int impressions;
    private final AudienceReach audienceReach;
    private final int cost;

    public static PromotionalPlan getPromotionalPlan(int impressions, AudienceReach audienceReach) {
        return Arrays.stream(PromotionalPlan.values())
            .filter(promotionalPlan -> promotionalPlan.impressions == impressions && promotionalPlan.audienceReach == audienceReach)
            .findFirst()
            .orElseThrow(() -> new IllegalEntityException(String.format("Promotional plan with impressions: %d and audience reach: %s doesn't exist.", impressions, audienceReach.name())));
    }
}
