package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class RecommendationRequestEvent {
    private Long id;
    private Long requesterId;
    private Long receiverId;
}
