package school.faang.user_service.dto.redis;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventRecommendationRequestDto {
    private Long requesterId;
    private Long receiverId;
    private Long recommendationId;
}
