package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.*;
import school.faang.user_service.mapper.PromotionMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotiom.PromotionRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PromotionMapper promotionMapper;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional
    public PromotionDto promoteUser(long userId, PromotionalPlan promotionalPlan) {
        if (promotionRepository.existsByUserId(userId)) {
            throw new AlreadyPurchasedException(String.format("User with ID: %d already has a promotion.", userId));
        }
        PaymentResponse paymentResponse = paymentServiceClient.sendPaymentRequest(
            new PaymentRequest(
                0,
                new BigDecimal(promotionalPlan.getCost()),
                Currency.USD
            )
        );
        if (paymentResponse.status() != PaymentStatus.SUCCESS) {
            throw new PaymentFailureException(String.format("Payment with payment number: %d failed.", paymentResponse.paymentNumber()));
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Event with ID: %d does not exist.", userId)));
        Promotion promotion = Promotion.builder()
            .user(user)
            .impressions(promotionalPlan.getImpressions())
            .audienceReach(promotionalPlan.getAudienceReach())
            .build();
        promotionRepository.save(promotion);
        return promotionMapper.toDto(promotion);
    }

    @Transactional
    public PromotionDto promoteEvent(long eventId, PromotionalPlan promotionalPlan) {
        if (promotionRepository.existsByEventId(eventId)) {
            throw new AlreadyPurchasedException(String.format("Event with ID: %d already has a promotion.", eventId));
        }
        PaymentResponse paymentResponse = paymentServiceClient.sendPaymentRequest(
            new PaymentRequest(
                0,
                new BigDecimal(promotionalPlan.getCost()),
                Currency.USD
            )
        );
        if (paymentResponse.status() != PaymentStatus.SUCCESS) {
            throw new PaymentFailureException(String.format("Payment with payment number: %d failed.", paymentResponse.paymentNumber()));
        }
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Event with ID: %d does not exist.", eventId)));
        Promotion promotion = Promotion.builder()
            .event(event)
            .impressions(promotionalPlan.getImpressions())
            .audienceReach(promotionalPlan.getAudienceReach())
            .build();
        promotionRepository.save(promotion);
        return promotionMapper.toDto(promotion);
    }
}
