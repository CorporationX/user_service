package school.faang.user_service.dto.promotion;

import school.faang.user_service.entity.promotion.PromotionalPlan;

public record PromotionDto(
    Long id,
    Long userId,
    Long eventId,
    PromotionalPlan promotionalPlan,
    int impressions
) {
}
