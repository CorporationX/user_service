package school.faang.user_service.validator.promotion;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.promotion.PromotionRepository;

@Component
@AllArgsConstructor
public class PromotionValidator {

    private final PromotionRepository promotionRepository;

    public void validatePromotionAlreadyExistsByUserId(Long userId) {
        if (promotionRepository.existsByUserId(userId)) {
            throw new DataValidationException("Promotion already exists for userId: " + userId);
        }
    }
}
