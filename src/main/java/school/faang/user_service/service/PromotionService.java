package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.paymentService.PaymentServiceClient;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.LinkEventPromotion;
import school.faang.user_service.entity.promotion.LinkUserPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.exception.promotion.PromotionIllegalArgumentException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.LinkEventPromotionRepository;
import school.faang.user_service.repository.promotion.LinkUserPromotionRepository;
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
    private final LinkUserPromotionRepository userPromotionRepository;
    private final LinkEventPromotionRepository eventPromotionRepository;
    private final PaymentServiceClient paymentClient;
    private final PromotionMapper promotionMapper;

    @Transactional
    public PromotionDto buyUserPromotion(long userId, long promotionId) {
        User user = findUserById(userId);
        PromotionTariff tariff = findTariffById(promotionId);
        saveUserPromotion(user, tariff);
        paymentClient.sendPayment(userId, tariff.getPriceUsd());
        return promotionMapper.tariffToPromotionDto(tariff);
    }

    @Transactional
    public PromotionDto buyEventPromotion(long eventId, long promotionId) {
        Event event = findEventById(eventId);
        PromotionTariff tariff = findTariffById(promotionId);
        saveEventPromotion(event, tariff);
        paymentClient.sendPayment(event.getId(), tariff.getPriceUsd());
        return promotionMapper.tariffToPromotionDto(tariff);
    }

    public List<PromotionDto> getAllPromotionTariffs() {
        List<PromotionTariff> tariffs = promotionRepository.findAll();
        return promotionMapper.tariffToPromotionDtoList(tariffs);
    }

    private void saveUserPromotion(User user, PromotionTariff tariff) {
        userPromotionRepository.save(LinkUserPromotion.builder()
                .active(true)
                .tariffId(tariff)
                .userId(user)
                .build());
    }

    private void saveEventPromotion(Event event, PromotionTariff tariff) {
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

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PromotionIllegalArgumentException("User not found id: " + userId));
    }

    private Event findEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new PromotionIllegalArgumentException("Event not found id: " + eventId));
    }
}
