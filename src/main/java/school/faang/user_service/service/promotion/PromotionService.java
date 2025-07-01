package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.event.EventRedisService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.user.UserRedisService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.promotion.PromotionValidator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static school.faang.user_service.entity.promotion.PromotionStatus.ACTIVE;
import static school.faang.user_service.entity.promotion.PromotionStatus.FINISHED_TIME;
import static school.faang.user_service.entity.promotion.PromotionStatus.FINISHED_VIEW;
import static school.faang.user_service.entity.promotion.PromotionType.EVENT;
import static school.faang.user_service.entity.promotion.PromotionType.USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final EventService eventService;
    private final UserService userService;
    private final PromotionTariffService promotionTariffService;
    private final PromotionRedisService promotionRedisService;
    private final EventRedisService eventRedisService;
    private final UserRedisService userRedisService;
    private final PromotionValidator promotionValidator;

    @Transactional(readOnly = true)
    public Promotion getPromotionById(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> {
                    log.error("Promotion with id {} not found", promotionId);
                    return new PromotionNotFoundException(promotionId);
                });
    }

    @Transactional
    public Promotion createPromotionForEvent(long eventId, long tariffId) {
        promotionValidator.checkActivePromotionForEvent(eventId);

        Event event = eventService.getEventById(eventId);
        PromotionTariff tariff = promotionTariffService.getPromotionTariffById(tariffId);

        Promotion promotion = createPromotion(tariff);
        promotion.setType(EVENT);
        promotion.setEvent(event);

        Promotion savePromotion = promotionRepository.save(promotion);
        log.info("Promotion {} has been created", savePromotion);

        promotionRedisService.savePromotion(savePromotion);

        long ttl = getTtlByPromotion(savePromotion);
        eventRedisService.saveEvent(event, ttl);

        return savePromotion;
    }

    @Transactional
    public Promotion createPromotionForUser(long userId, long tariffId) {
        promotionValidator.checkActivePromotionForUser(userId);

        User user = userService.getUserByIdOrThrow(userId);
        PromotionTariff tariff = promotionTariffService.getPromotionTariffById(tariffId);

        Promotion promotion = createPromotion(tariff);
        promotion.setType(USER);
        promotion.setUser(user);

        Promotion savePromotion = promotionRepository.save(promotion);
        log.info("Promotion {} has been created", savePromotion);

        promotionRedisService.savePromotion(savePromotion);

        long ttl = getTtlByPromotion(savePromotion);
        userRedisService.saveUser(user, ttl);

        return savePromotion;
    }

    private Promotion createPromotion(PromotionTariff tariff) {
        Promotion promotion = new Promotion();
        promotion.setTariff(tariff);
        promotion.setCountView(tariff.getCountView());
        promotion.setEndDate(LocalDateTime.now().plusDays(tariff.getDurationDays()));
        promotion.setStatus(ACTIVE);
        return promotion;
    }

    @Transactional
    public void finishPromotionByView(long promotionId) {
        Promotion promotion = getPromotionById(promotionId);

        promotion.setStatus(FINISHED_VIEW);
        promotionRepository.save(promotion);
        log.info("Promotion with id {} finished by view", promotionId);
    }

    @Transactional
    public void finishPromotionByTime(long promotionId) {
        Promotion promotion = getPromotionById(promotionId);

        promotion.setStatus(FINISHED_TIME);
        promotionRepository.save(promotion);
        log.info("Promotion with id {} finished by time", promotionId);
    }

    // TODO: тесты
    @Transactional(readOnly = true)
    public List<Promotion> getAllActivePromotion(PromotionType type) {
        return promotionRepository.findAllByTypeAndStatus(type, ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Promotion> getAllActivePromotion() {
        return promotionRepository.findAllByStatus(ACTIVE);
    }

    private long getTtlByPromotion(Promotion promotion) {
        long seconds = Duration.between(LocalDateTime.now(), promotion.getEndDate()).getSeconds();
        return seconds > 0 ? seconds : 0;
    }
}
