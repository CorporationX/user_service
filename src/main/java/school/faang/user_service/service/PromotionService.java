package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import school.faang.user_service.model.dto.PromotionDto;
import school.faang.user_service.model.enums.PromotionType;

public interface PromotionService {
    @Transactional
    PromotionDto buyPromotion(long userId, PromotionType type, String target);
}
