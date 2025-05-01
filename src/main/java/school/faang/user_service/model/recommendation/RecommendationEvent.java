package school.faang.user_service.model.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecommendationEvent {
    Long recommendationId;
    Long authorId;
    Long receiverId;
    LocalDateTime createdAt;
}
