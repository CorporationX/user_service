package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecommendationEvent {

    private long recommendationId;
    private long authorId;
    private long receiverId;
    private LocalDateTime timestamp;

}
