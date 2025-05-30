package school.faang.user_service.service.promotion.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dao.promotion.PromotionUpdatorService;
import school.faang.user_service.exception.BatchUpdateProcessingException;
import school.faang.user_service.kafka.EventType;
import school.faang.user_service.redis.RedisAnalyticsService;

import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class EventTypeBatchProcessor {
    private final RedisAnalyticsService redisAnalyticsService;
    private final PromotionUpdatorService promotionUpdatorService;

    public void process(EventType eventType) {
        Map<Long, Long> idsAboveThreshold = redisAnalyticsService.getIdsAboveThreshold(eventType);
        if (idsAboveThreshold.isEmpty()) {
            log.debug("Nothing to flush for {}", eventType);
            return;
        }
        try {
            promotionUpdatorService.batchUpdatePromotions(eventType, idsAboveThreshold);
        } catch (Exception e) {
            log.warn("Batch update promotions failed", e);
            throw new BatchUpdateProcessingException("Batch update promotions failed", e);
        }
        redisAnalyticsService.removeProcessedKeys(eventType, idsAboveThreshold.keySet());
    }
}


