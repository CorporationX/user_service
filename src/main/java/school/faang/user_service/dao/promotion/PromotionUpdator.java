package school.faang.user_service.dao.promotion;

import school.faang.user_service.kafka.EventType;

import java.util.Map;

public interface PromotionUpdator {
    void batchUpdatePromotions(Map<Long, Long> idsScoresMap);
    EventType getEventType();
}
