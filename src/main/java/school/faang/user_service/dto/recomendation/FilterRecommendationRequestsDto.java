package school.faang.user_service.dto.recomendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class FilterRecommendationRequestsDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private RequestStatus status;
}