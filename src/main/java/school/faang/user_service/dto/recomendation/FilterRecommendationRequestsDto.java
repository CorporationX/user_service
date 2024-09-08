package school.faang.user_service.dto.recomendation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterRecommendationRequestsDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private RequestStatus status;
}