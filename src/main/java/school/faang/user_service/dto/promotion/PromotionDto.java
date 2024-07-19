package school.faang.user_service.dto.promotion;

import lombok.Builder;
import school.faang.user_service.entity.promotion.PromotionalPlan;

@Builder
public record PromotionDto(
    Long id,
    Long userId,
    Long eventId,
    PromotionalPlan promotionalPlan,
    int impressions
) {
}
