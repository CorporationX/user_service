package school.faang.user_service.job.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.promotion.PromotionRedisModel;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.promotion.PromotionService;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PromotionCleanByViewJob {
    private final PromotionService promotionService;
    private final PromotionRedisService promotionRedisService;

    @Scheduled(cron = "${jobs.promotion.close.view.cron}")
    public void cleanupDeletedPromotionsByView() {
        log.info("Job promotion clean by view started");
        List<PromotionRedisModel> promotions = promotionRedisService.getAllPromotions();
        promotions.forEach(promotion -> {
            if (promotion.getCountView() <= 0) {
                promotionRedisService.deletePromotionByKey(promotion.getKey());
                promotionService.finishPromotionByView(promotion.getId());
                log.debug("Job promotion clean finished promotion by view with id {} on views", promotion.getId());
            }
        });
        log.info("Job promotion clean by view finished");
    }
}
