package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.promotion.util.PromotionBuilder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.UNSUCCESSFUL_EVENT_PROMOTION_PAYMENT;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.UNSUCCESSFUL_USER_PROMOTION_PAYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {
    private static final int THREAD_POOL_SIZE = 10;

    private final UserPromotionRepository userPromotionRepository;
    private final EventPromotionRepository eventPromotionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PaymentService paymentService;
    private final PromotionTaskService promotionTaskService;
    private final PromotionValidationService promotionValidationService;
    private final PromotionBuilder promotionBuilder;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @Transactional
    public UserPromotion buyPromotion(long userId, PromotionTariff tariff) {
        log.info("User with id: {} buy promotion tariff: {}", userId, tariff.toString());
        User user = promotionValidationService.checkUserForPromotion(userId);
        PaymentResponseDto paymentResponse = paymentService.sendPayment(tariff);
        promotionValidationService
                .checkPromotionPaymentResponse(paymentResponse, userId, tariff, UNSUCCESSFUL_USER_PROMOTION_PAYMENT);
        UserPromotion promotion = promotionBuilder.getUserPromotion(user, tariff);
        if (user.getPromotion() != null) {
            userPromotionRepository.delete(user.getPromotion());
        }
        return userPromotionRepository.save(promotion);
    }

    @Transactional
    public EventPromotion buyEventPromotion(long userId, long eventId, PromotionTariff tariff) {
        log.info("User with id: {} buy promotion tariff: {} for event id: {}", userId, tariff.toString(), eventId);
        Event event = promotionValidationService.checkEventForUserAndPromotion(userId, eventId);
        PaymentResponseDto paymentResponse = paymentService.sendPayment(tariff);
        promotionValidationService
                .checkPromotionPaymentResponse(paymentResponse, eventId, tariff, UNSUCCESSFUL_EVENT_PROMOTION_PAYMENT);
        EventPromotion eventPromotion = promotionBuilder.getEventPromotion(event, tariff);
        if (event.getPromotion() != null) {
            eventPromotionRepository.delete(event.getPromotion());
        }
        return eventPromotionRepository.save(eventPromotion);
    }

    @Transactional
    public List<User> getPromotedUsersBeforeAllPerPage(int limit, int offset) {
        log.info("Get promoted users before all per page: {} - {}", limit, offset);
        List<User> users = userRepository.findAllSortedByPromotedUsersPerPage(limit, offset);
        executorService.execute(() -> promotionTaskService.decrementUserPromotionViews(users));
        return users;
    }

    @Transactional
    public List<Event> getPromotedEventsBeforeAllPerPage(int limit, int offset) {
        log.info("Get promoted events before all per page: {} - {}", limit, offset);
        List<Event> events = eventRepository.findAllSortedByPromotedEventsPerPage(limit, offset);
        executorService.execute(() -> promotionTaskService.decrementEventPromotionViews(events));
        return events;
    }
}
