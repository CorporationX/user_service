package school.faang.user_service.service.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.promotion.PromotionValidationException;

import java.util.List;
import java.util.Optional;

import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.EVENT_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.USER_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.USER_NOT_OWNER_OF_EVENT;

@Slf4j
@Service
public class PromotionValidationService {

    public void checkUserForPromotion(User user) {
        getActiveUserPromotion(user).ifPresent(promotion -> {
            throw new PromotionValidationException(USER_ALREADY_HAS_PROMOTION, user.getId(), promotion.getNumberOfViews());
        });
    }

    public Optional<UserPromotion> getActiveUserPromotion(User user) {
        return user.getPromotions()
                .stream()
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .findFirst();
    }

    public List<UserPromotion> getActiveUserPromotions(List<User> users) {
        return users.stream()
                .map(this::getActiveUserPromotion)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public void checkEventForUserAndPromotion(long userId, long eventId, Event event) {
        if (userId != event.getOwner().getId()) {
            throw new PromotionValidationException(USER_NOT_OWNER_OF_EVENT, userId, eventId);
        }
        getActiveEventPromotion(event).ifPresent(promotion -> {
            throw new PromotionValidationException(EVENT_ALREADY_HAS_PROMOTION, eventId, promotion.getNumberOfViews());
        });
    }

    public Optional<EventPromotion> getActiveEventPromotion(Event event) {
        return event.getPromotions()
                .stream()
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .findFirst();
    }

    public List<EventPromotion> getActiveEventPromotions(List<Event> events) {
        return events.stream()
                .map(this::getActiveEventPromotion)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public void checkPromotionPaymentResponse(PaymentResponseDto paymentResponse, long id, PromotionTariff tariff,
                                              String errorMessage) {
        log.info("Check promotion payment response: {}", paymentResponse);
        if (!paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new UnSuccessPaymentException(errorMessage, tariff.getNumberOfViews(), id, paymentResponse.message());
        }
    }
}
