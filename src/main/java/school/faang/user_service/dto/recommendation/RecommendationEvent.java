package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationEvent {
    private long recommendationId;
    private long authorId;
    private long receiverId;
    private LocalDateTime createdAt;
}