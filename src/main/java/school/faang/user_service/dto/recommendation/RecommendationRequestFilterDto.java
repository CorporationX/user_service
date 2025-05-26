package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class RecommendationRequestFilterDto {
    private String message;
    private Long requesterId;
    private Long receiverId;
}
