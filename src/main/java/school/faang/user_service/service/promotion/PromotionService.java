package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {
    private static final int THREAD_POOL_SIZE = 10;

    private final UserPromotionRepository userPromotionRepository;
    private final EventPromotionRepository eventPromotionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PromotionTaskService promotionTaskService;
    private final PromotionCheckService promotionCheckService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @Transactional
    public UserPromotion buyPromotion(long userId, PromotionTariff tariff) {
        User user = promotionCheckService.checkUserForPromotion(userId);
        PaymentResponse paymentResponse = sendPayment(tariff);
        promotionCheckService.checkUserPromotionPaymentResponse(paymentResponse, userId, tariff);
        var promotion = new UserPromotion(null, tariff, tariff.getCoefficient(), user,
                tariff.getNumberOfViews(), tariff.getAudienceReach(), LocalDateTime.now());
        if (user.getPromotion() != null) {
            userPromotionRepository.delete(user.getPromotion());
        }
        return userPromotionRepository.save(promotion);
    }

    @Transactional
    public EventPromotion buyEventPromotion(long userId, long eventId, PromotionTariff tariff) {
        Event event = promotionCheckService.checkEventForUserAndPromotion(userId, eventId);
        PaymentResponse paymentResponse = sendPayment(tariff);
        promotionCheckService.checkEventPromotionPaymentResponse(paymentResponse, eventId, tariff);
        var eventPromotion = new EventPromotion(null, tariff, tariff.getCoefficient(), event,
                tariff.getNumberOfViews(), tariff.getAudienceReach(), LocalDateTime.now());
        if (event.getPromotion() != null) {
            eventPromotionRepository.delete(event.getPromotion());
        }
        return eventPromotionRepository.save(eventPromotion);
    }

    @Transactional
    public List<User> getPromotedUsersBeforeAllPerPage(int limit, int offset) {
        List<User> users = userRepository.findAllSortedByPromotedUsersPerPage(limit, offset);
        executorService.execute(() -> promotionTaskService.decrementUserPromotionViews(users));
        return users;
    }

    @Transactional
    public List<Event> getPromotedEventsBeforeAllPerPage(int limit, int offset) {
        List<Event> events = eventRepository.findAllSortedByPromotedEventsPerPage(limit, offset);
        executorService.execute(() -> promotionTaskService.decrementEventPromotionViews(events));
        return events;
    }

    private PaymentResponse sendPayment(PromotionTariff tariff) {
        var paymentRequest = PaymentRequest
                .builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(BigDecimal.valueOf(tariff.getCost()))
                .currency(tariff.getCurrency())
                .build();
        return paymentServiceClient.sendPayment(paymentRequest);
    }
}
