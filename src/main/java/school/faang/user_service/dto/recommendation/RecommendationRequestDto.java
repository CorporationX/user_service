package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

@Data
@Builder
public class RecommendationRequestDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private RequestStatus status;
    private List<Long> skillIds;
}
