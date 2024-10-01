package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.promotion.batch.EventPromotionRepositoryBatch;
import school.faang.user_service.repository.promotion.batch.UserPromotionRepositoryBatch;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionTaskService {
    private final UserPromotionRepositoryBatch userPromotionRepositoryBatch;
    private final EventPromotionRepositoryBatch eventPromotionRepositoryBatch;
    private final Map<Long, Integer> userPromotionViews = new ConcurrentHashMap<>();
    private final Map<Long, Integer> eventPromotionViews = new ConcurrentHashMap<>();
    private final AtomicBoolean isUserViewsDecrementRunning = new AtomicBoolean(false);
    private final AtomicBoolean isEventViewsDecrementRunning = new AtomicBoolean(false);

    @Async("promotionTaskServicePool")
    public void decrementUserPromotionViews(List<UserPromotion> userPromotions) {
        log.info("Decrement user promotion views");
        userPromotions.forEach(userPromotion -> {
            long promotionId = userPromotion.getId();
            int views = userPromotionViews.computeIfAbsent(promotionId, k -> 0);
            userPromotionViews.put(promotionId, ++views);
        });
    }

    @Scheduled(cron = "${app.promotion.user_promotion_views_decrement_cron}")
    public void executeUserPromotionViewsDecrement() {
        if (!userPromotionViews.isEmpty() && isUserViewsDecrementRunning.compareAndSet(false, true)) {
            decrementUserPromotionView();
        }
    }

    @Async("promotionTaskServicePool")
    public void decrementEventPromotionViews(List<EventPromotion> eventPromotions) {
        log.info("Decrement event promotion");
        eventPromotions.forEach(eventPromotion -> {
            long promotionId = eventPromotion.getId();
            int views = eventPromotionViews.computeIfAbsent(promotionId, k -> 0);
            eventPromotionViews.put(promotionId, ++views);
        });
    }

    @Scheduled(cron = "${app.promotion.event__promotion_views_decrement_cron}")
    public void executeEventPromotionViewsDecrement() {
        if (!eventPromotionViews.isEmpty() && isEventViewsDecrementRunning.compareAndSet(false, true)) {
            decrementEventPromotionViews();
        }
    }

    private void decrementUserPromotionView() {
        Map<Long, Integer> userPromotionViewsCopy = new HashMap<>();
        try {
            log.info("User promotion views batch decrement: {}", userPromotionViews);
            synchronized (userPromotionViews) {
                userPromotionViewsCopy = new HashMap<>(userPromotionViews);
                userPromotionViews.clear();
            }
            userPromotionRepositoryBatch.updateUserPromotions(userPromotionViewsCopy);
        } catch (SQLException | UncategorizedSQLException e) {
            log.error("SQL Exception when decrement user promotion views: {}", e.getMessage());
            synchronized (userPromotionViews) {
                userPromotionViews.putAll(userPromotionViewsCopy);
            }
        } finally {
            isUserViewsDecrementRunning.set(false);
        }
    }

    private void decrementEventPromotionViews() {
        Map<Long, Integer> eventPromotionViewsCopy = new HashMap<>();
        try {
            log.info("Event promotion views batch decrement: {}", eventPromotionViews);
            synchronized (eventPromotionViews) {
                eventPromotionViewsCopy = new HashMap<>(eventPromotionViews);
                eventPromotionViews.clear();
            }
            eventPromotionRepositoryBatch.updateEventPromotions(eventPromotionViewsCopy);
        } catch (SQLException | UncategorizedSQLException e) {
            log.error("SQL Exception when decrement event promotion views: {}", e.getMessage());
            synchronized (eventPromotionViews) {
                eventPromotionViews.putAll(eventPromotionViewsCopy);
            }
        } finally {
            isEventViewsDecrementRunning.set(false);
        }
    }
}
