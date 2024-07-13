package school.faang.user_service.dto;

import school.faang.user_service.entity.promotion.AudienceReach;

public record PromotionDto(
    Long id,
    Long userId,
    Long eventId,
    int impressions,
    AudienceReach audienceReach
) {
}
