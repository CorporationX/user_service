package school.faang.user_service.dao.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.EventType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PromotionUpdatorService {
    private final Map<EventType, PromotionUpdator> promotionUpdatorMap;

    public PromotionUpdatorService(List<PromotionUpdator> promotionUpdatorList) {
        this.promotionUpdatorMap = promotionUpdatorList.stream()
                .collect(Collectors.toMap(PromotionUpdator::getEventType,
                        Function.identity()));
    }

    public void batchUpdatePromotions(@NotNull(message = "EventType cannot be null") EventType eventType,
                                      @NotNull(message = "Passed IdsScores Map cannot be null") Map<Long, Long> idsScores) {
        PromotionUpdator promotionUpdator = promotionUpdatorMap.get(eventType);
        if (promotionUpdator == null) {
            throw new IllegalArgumentException("No processor for " + eventType);
        }
        promotionUpdator.batchUpdatePromotions(idsScores);
    }
}
