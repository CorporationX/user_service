package school.faang.user_service.dto.promotion.user;

import school.faang.user_service.enums.promotion.PromotionPriority;
import school.faang.user_service.enums.promotion.user.UserPromotionType;

import java.time.LocalDateTime;

public record UserPromotionDto(
        LocalDateTime startDate,
        LocalDateTime endDate,
        UserPromotionType userPromotionType,
        PromotionPriority promotionPriority
) {
}
