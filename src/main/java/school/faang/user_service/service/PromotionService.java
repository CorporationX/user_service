package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.paymentService.PaymentServiceClient;
import school.faang.user_service.client.paymentService.model.EntityType;
import school.faang.user_service.client.paymentService.model.PaymentRequest;
import school.faang.user_service.client.paymentService.model.PaymentStatus;
import school.faang.user_service.client.paymentService.model.Product;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.LinkEventPromotion;
import school.faang.user_service.entity.promotion.LinkUserPromotion;
import school.faang.user_service.entity.promotion.PromotionPaymentRequest;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.exception.promotion.PromotionIllegalArgumentException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.LinkEventPromotionRepository;
import school.faang.user_service.repository.promotion.LinkUserPromotionRepository;
import school.faang.user_service.repository.promotion.PromotionPaymentRequestRepository;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionPaymentRequestRepository paymentRequestRepository;
    private final LinkUserPromotionRepository userPromotionRepository;
    private final LinkEventPromotionRepository eventPromotionRepository;
    private final PaymentServiceClient paymentClient;
    private final PromotionMapper promotionMapper;

    @Transactional
    public PaymentRequest buyUserPromotion(long userId, long promotionId) {
        checkIfExistsUser(userId);
        PromotionTariff tariff = findTariffById(promotionId);
        PromotionPaymentRequest paymentRequest = paymentRequestRepository.save(
                PromotionPaymentRequest.builder()
                        .entityType(EntityType.USER.toString())
                        .tariffId(tariff)
                        .status(PaymentStatus.CREATED.toString())
                        .entityId(userId)
                        .build());
        return paymentClient.sendPaymentRequest(paymentRequest.getId().toString(),
                tariff.getPriceUsd(), Product.PROMOTION);
    }

    @Transactional
    public PaymentRequest buyEventPromotion(long eventId, long promotionId) {
        checkIfExistsEvent(eventId);
        PromotionTariff tariff = findTariffById(promotionId);
        PromotionPaymentRequest paymentRequest = paymentRequestRepository.save(
                PromotionPaymentRequest.builder()
                        .entityType(EntityType.EVENT.toString())
                        .tariffId(tariff)
                        .status(PaymentStatus.CREATED.toString())
                        .entityId(eventId)
                        .build());
        return paymentClient.sendPaymentRequest(paymentRequest.getId().toString(),
                tariff.getPriceUsd(), Product.PROMOTION);
    }

    public List<PromotionDto> getAllPromotionTariffs() {
        List<PromotionTariff> tariffs = promotionRepository.findAll();
        return promotionMapper.tariffToPromotionDtoList(tariffs);
    }

    @Transactional
    public void savePromotion(EntityType entityType, long entityId, long tariffId) {
        switch (entityType) {
            case USER -> saveUserPromotion(entityId, tariffId);
            case EVENT -> saveEventPromotion(entityId, tariffId);
        }
        log.info("Saved promotion entity: {}, entityId: {} tariffId: {}",
                entityType, entityId, tariffId);
    }

    private void saveUserPromotion(long userId, long tariffId) {
        User user = findUserById(userId);
        PromotionTariff tariff = findTariffById(tariffId);
        userPromotionRepository.save(LinkUserPromotion.builder()
                .active(true)
                .tariffId(tariff)
                .userId(user)
                .build());
    }

    private void saveEventPromotion(long eventId, long tariffId) {
        Event event = findEventById(eventId);
        PromotionTariff tariff = findTariffById(tariffId);
        eventPromotionRepository.save(LinkEventPromotion.builder()
                .active(true)
                .tariffId(tariff)
                .eventId(event)
                .build());
    }

    private PromotionTariff findTariffById(long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new PromotionIllegalArgumentException("Promotion not found id: " + promotionId));
    }

    private void checkIfExistsUser(long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new PromotionIllegalArgumentException("User with id: " + userId + " not found");
        }
    }

    private void checkIfExistsEvent(long eventId) {
        boolean exists = eventRepository.existsById(eventId);
        if (!exists) {
            throw new PromotionIllegalArgumentException("Event with id: " + eventId + " not found");
        }
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PromotionIllegalArgumentException("User not found id: " + userId));
    }

    private Event findEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new PromotionIllegalArgumentException("Event not found id: " + eventId));
    }
}
