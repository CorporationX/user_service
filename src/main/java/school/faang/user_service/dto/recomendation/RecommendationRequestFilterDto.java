package school.faang.user_service.dto.recomendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;


@Data
public class RecommendationRequestFilterDto {
    private Long requestIdPattern;
    private Long receiverIdPattern;
    private String messagePattern;
    private RequestStatus statusPattern;
}
