package school.faang.user_service.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.EventType;
import school.faang.user_service.redis.AnalyticsProperties;
import school.faang.user_service.service.promotion.batch.EventTypeBatchProcessor;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionBatchRunner {
    private final AnalyticsProperties analyticsProperties;
    private final EventTypeBatchProcessor eventTypeBatchProcessor;

    @Scheduled(fixedDelayString = "${analytics.flush-interval-ms:10000}")
    public void runBatch() {
        log.info("Promotion Batch running");
        for (EventType eventType : analyticsProperties.getAllowed()) {
            eventTypeBatchProcessor.process(eventType);
        }
    }
}
