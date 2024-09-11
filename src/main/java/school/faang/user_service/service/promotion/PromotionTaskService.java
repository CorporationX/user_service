package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionTaskService {
    private final UserPromotionRepository userPromotionRepository;
    private final EventPromotionRepository eventPromotionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrementUserPromotionViews(List<User> users) {
        users.stream()
                .filter(user -> user.getPromotion() != null)
                .forEach(user -> {
                    try {
                        decrementUserPromotionView(user.getPromotion().getId());
                    } catch (Exception exc) {
                        log.info("Failed to decrement user promotion views for user id: {}, message: {}", user.getId(),
                                exc.getMessage());
                    }
                });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrementUserPromotionView(long id) {
        Optional<UserPromotion> userPromotionOpt = userPromotionRepository.findByIdWithLock(id);
        userPromotionOpt.ifPresentOrElse(userPromotion -> {
            if (userPromotion.getNumberOfViews() <= 1) {
                userPromotionRepository.delete(userPromotion);
            } else {
                userPromotionRepository.decrementPromotionViews(userPromotion.getId());
            }
        }, () -> log.info("User promotion with id: {} not found", id));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrementEventPromotionViews(List<Event> events) {
        events.stream()
                .filter(event -> event.getPromotion() != null)
                .forEach(event -> {
                    try {
                        decrementEventPromotionView(event.getPromotion().getId());
                    } catch (Exception exc) {
                        log.info("Failed to decrement event promotion views for user id: {}, message: {}", event.getId(),
                                exc.getMessage());
                    }
                });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrementEventPromotionView(long id) {
        Optional<EventPromotion> eventPromotionOpt = eventPromotionRepository.findByIdWithLock(id);
        eventPromotionOpt.ifPresentOrElse(eventPromotion -> {
            if (eventPromotion.getNumberOfViews() <= 1) {
                eventPromotionRepository.delete(eventPromotion);
            } else {
                eventPromotionRepository.decrementPromotionViews(eventPromotion.getId());
            }
        }, () -> log.info("Event promotion with id: {} not found", id));
    }
}
