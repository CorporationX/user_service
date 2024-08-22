package school.faang.user_service.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecommendationEventPublisher extends Event {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private LocalDateTime receivedAt;
}
