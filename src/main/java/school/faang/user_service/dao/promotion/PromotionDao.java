package school.faang.user_service.dao.promotion;

import school.faang.user_service.kafka.events.AnalyticsEventType;

import java.util.List;
import java.util.Map;

public interface PromotionDao {
    List<Long> batchUpdatePromotions(Map<Long, Long> idsScoresMap);

    AnalyticsEventType getEventType();
}
