package school.faang.user_service.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class RecommendationEventPublisher extends Event {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private LocalDateTime receivedAt;
}
