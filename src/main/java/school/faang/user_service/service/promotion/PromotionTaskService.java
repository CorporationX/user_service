package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.promotion.EventPromotionRepositoryBatch;
import school.faang.user_service.repository.promotion.UserPromotionRepositoryBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionTaskService {

    @Value("${app.promotion.scheduled_decrement_pool_size}")
    private int threadPoolSize;

    @Value("${app.promotion.user_views_decrement_schedule_time}")
    private int userViewsDecrementScheduleTime;

    @Value("${app.promotion.event_views_decrement_schedule_time}")
    private int eventViewsDecrementScheduleTime;

    private final UserPromotionRepositoryBatch userPromotionRepositoryBatch;
    private final EventPromotionRepositoryBatch eventPromotionRepositoryBatch;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadPoolSize);
    private final Map<Long, Integer> userPromotionViews = new ConcurrentHashMap<>();
    private final Map<Long, Integer> eventPromotionViews = new ConcurrentHashMap<>();
    private final AtomicBoolean isUserViewsDecrementRunning = new AtomicBoolean(false);
    private final AtomicBoolean isEventViewsDecrementRunning = new AtomicBoolean(false);

    @Async
    public void decrementUserPromotionViews(List<UserPromotion> userPromotions) {
        log.info("Decrement user promotion views");
        userPromotions.forEach(userPromotion -> {
            long promotionId = userPromotion.getId();
            int views = userPromotionViews.computeIfAbsent(promotionId, k -> 0);
            userPromotionViews.put(promotionId, ++views);
        });
        if (isUserViewsDecrementRunning.compareAndSet(false, true)) {
            scheduler.schedule(this::executeUserPromotionViewsDecrement, userViewsDecrementScheduleTime,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void executeUserPromotionViewsDecrement() {
        try {
            log.info("User promotion views batch decrement: {}", userPromotionViews);
            Map<Long, Integer> userPromotionViewsCopy = new HashMap<>(userPromotionViews);
            userPromotionViews.clear();
            userPromotionRepositoryBatch.updateUserPromotions(userPromotionViewsCopy);
        } finally {
            isUserViewsDecrementRunning.set(false);
        }
    }

    @Async
    public void decrementEventPromotionViews(List<EventPromotion> eventPromotions) {
        log.info("Decrement event promotion");
        eventPromotions.forEach(eventPromotion -> {
            long promotionId = eventPromotion.getId();
            int views = eventPromotionViews.computeIfAbsent(promotionId, k -> 0);
            eventPromotionViews.put(promotionId, ++views);
        });
        if (isEventViewsDecrementRunning.compareAndSet(false, true)) {
            scheduler.schedule(this::executeEventPromotionViewsDecrement, eventViewsDecrementScheduleTime,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void executeEventPromotionViewsDecrement() {
        try {
            log.info("Event promotion views batch decrement: {}", eventPromotionViews);
            Map<Long, Integer> eventPromotionViewsCopy = new HashMap<>(eventPromotionViews);
            eventPromotionViews.clear();
            eventPromotionRepositoryBatch.updateEventPromotions(eventPromotionViewsCopy);
        } finally {
            isEventViewsDecrementRunning.set(false);
        }
    }
}
