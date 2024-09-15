package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RecommendationRequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
