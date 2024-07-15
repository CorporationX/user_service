package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.AlreadyPurchasedException;
import school.faang.user_service.repository.promotiom.PromotionRepository;

@Component
@RequiredArgsConstructor
public class PromotionValidator {
    private final PromotionRepository promotionRepository;

    public void validateUserAlreadyHasPromotion(long userId) {
        if (promotionRepository.existsByUserId(userId)) {
            throw new AlreadyPurchasedException(String.format("User with ID: %d already has a promotion.", userId));
        }
    }

    public void validateEventAlreadyHasPromotion(long eventId) {
        if (promotionRepository.existsByEventId(eventId)) {
            throw new AlreadyPurchasedException(String.format("Event with ID: %d already has a promotion.", eventId));
        }
    }
}
