package school.faang.user_service.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.events.AnalyticsEventType;
import school.faang.user_service.redis.promotion.PromotionAnalyticsProperties;
import school.faang.user_service.service.promotion.PromotionBatchProcessor;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionBatchRunner {
    private final PromotionAnalyticsProperties promotionAnalyticsProperties;
    private final PromotionBatchProcessor promotionBatchProcessor;

    @Scheduled(fixedDelayString = "${analytics.promotion.flush-interval-ms:10000}")
    public void runBatch() {
        log.info("Promotion Batch running");
        for (AnalyticsEventType analyticsEventType : promotionAnalyticsProperties.getAllowed()) {
            promotionBatchProcessor.process(analyticsEventType);
        }
    }
}
