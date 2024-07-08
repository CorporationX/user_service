package school.faang.user_service.dto.event.redis;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecommendationEventDto extends Event {
    private Long recommendationId;
    private Long authorId;
    private Long receiverId;
}
