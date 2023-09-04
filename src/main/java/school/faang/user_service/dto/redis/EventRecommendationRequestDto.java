package school.faang.user_service.dto.redis;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventRecommendationRequestDto {

    private Long recommendationId;

    private Long actorId;

    private LocalDateTime receivedAt;
}