package school.faang.user_service.service.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dao.promotion.PromotionDao;
import school.faang.user_service.exception.BatchUpdateProcessingException;
import school.faang.user_service.kafka.events.AnalyticsEventType;
import school.faang.user_service.redis.promotion.PromotionAnalyticsCacheService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@Slf4j
public class PromotionBatchProcessor {
    private final PromotionAnalyticsCacheService cacheService;
    private final Map<AnalyticsEventType, PromotionDao> promotionDaoMap;

    public PromotionBatchProcessor(PromotionAnalyticsCacheService cacheService,
                                   List<PromotionDao> promotionDaoList) {
        this.cacheService = cacheService;
        this.promotionDaoMap = promotionDaoList.stream()
                .collect(Collectors.toMap(PromotionDao::getEventType,
                        Function.identity()));
    }

    public void process(@NotNull(message = "EventType cannot be null") AnalyticsEventType analyticsEventType) {
        Map<Long, Long> idsToScores = cacheService.getIdsScoreAboveThreshold(analyticsEventType);
        if (idsToScores.isEmpty()) {
            log.debug("No ids with score above the threshold found for {}", analyticsEventType);
            return;
        }
        List<Long> successUpdates;
        try {
            successUpdates = batchUpdatePromotions(analyticsEventType, idsToScores);
        } catch (Exception e) {
            log.warn("Batch update promotions failed", e);
            throw new BatchUpdateProcessingException("Batch update promotions failed", e);
        }
        cacheService.removeProcessedKeys(analyticsEventType, successUpdates);
    }

    private List<Long> batchUpdatePromotions(AnalyticsEventType analyticsEventType, Map<Long, Long> idsScores) {
        PromotionDao promotionDao = promotionDaoMap.get(analyticsEventType);
        if (promotionDao == null) {
            throw new IllegalArgumentException("No processor for " + analyticsEventType);
        }
        return promotionDao.batchUpdatePromotions(idsScores);
    }
}


