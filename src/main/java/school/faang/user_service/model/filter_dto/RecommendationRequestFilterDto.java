package school.faang.user_service.model.filter_dto;

import lombok.Data;
import school.faang.user_service.model.enums.RequestStatus;


@Data
public class RecommendationRequestFilterDto {
    private Long requestIdPattern;
    private Long receiverIdPattern;
    private String messagePattern;
    private RequestStatus statusPattern;
}
