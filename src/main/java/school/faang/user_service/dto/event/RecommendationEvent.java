package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class RecommendationEvent {
    private Long recommenderId;
    private Long recommendedId;
    private String recommendationText;
}
