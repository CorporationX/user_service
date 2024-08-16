package school.faang.user_service.event.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationEvent {
    private long recommendationId;
    private long authorId;
    private long receiverId;
    private LocalDateTime timestamp;
}