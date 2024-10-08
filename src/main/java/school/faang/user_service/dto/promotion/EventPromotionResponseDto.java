package school.faang.user_service.dto.promotion;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventPromotionResponseDto(
        long id,
        long eventId,
        int numberOfViews,
        int audienceReach,
        LocalDateTime creationDate
) {
}
