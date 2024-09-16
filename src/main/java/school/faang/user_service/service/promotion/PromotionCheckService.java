package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.promotion.PromotionCheckException;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_NOT_FOUND;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.EVENT_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.EVENT_NOT_FOUND_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.USER_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.USER_NOT_OWNER_OF_EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionCheckService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public User checkUserForPromotion(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new PromotionNotFoundException(USER_NOT_FOUND, userId));
        UserPromotion userPromotion = user.getPromotion();
        if (userPromotion != null && userPromotion.getNumberOfViews() > 0) {
            throw new PromotionCheckException(USER_ALREADY_HAS_PROMOTION, userId, userPromotion.getNumberOfViews());
        }
        return user;
    }

    @Transactional(readOnly = true)
    public Event checkEventForUserAndPromotion(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new PromotionNotFoundException(EVENT_NOT_FOUND_PROMOTION, eventId));
        if (userId != event.getOwner().getId()) {
            throw new PromotionCheckException(USER_NOT_OWNER_OF_EVENT, userId, eventId);
        }
        EventPromotion eventPromotion = event.getPromotion();
        if (eventPromotion != null && eventPromotion.getNumberOfViews() > 0) {
            throw new PromotionCheckException(EVENT_ALREADY_HAS_PROMOTION, eventId, eventPromotion.getNumberOfViews());
        }
        return event;
    }

    public void checkPromotionPaymentResponse(PaymentResponse paymentResponse, long id, PromotionTariff tariff,
                                              String errorMessage) {
        log.info("Check promotion payment response: {}", paymentResponse);
        if (!paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new UnSuccessPaymentException(errorMessage, tariff.getNumberOfViews(), id, paymentResponse.message());
        }
    }
}
