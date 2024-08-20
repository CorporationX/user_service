package school.faang.user_service.event.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationEvent {
    private final UUID eventId = UUID.randomUUID();
    private long recommendationId;
    private long authorId;
    private long receiverId;
    private LocalDateTime timestamp;
}