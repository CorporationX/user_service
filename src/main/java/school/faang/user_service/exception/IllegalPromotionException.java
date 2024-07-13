package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.entity.promotion.AudienceReach;

@Slf4j
public class IllegalPromotionException extends IllegalArgumentException {
    public IllegalPromotionException(String audienceReachName) {
        super(String.format("No audience reach found with name: %s.", audienceReachName));
        log.error("No audience reach found with name: {}.", audienceReachName, this);
    }

    public IllegalPromotionException(int impressions, AudienceReach audienceReach) {
        super(String.format("No promotion found with impressions: %d and audienceReach: %s.", impressions, audienceReach.name()));
        log.error("No promotion found with impressions: {} and audienceReach: {}.", impressions, audienceReach.name(), this);
    }
}
