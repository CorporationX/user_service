package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.util.Set;

@Data
@AllArgsConstructor
public class RecommendationRequestFilterDto {
    Long requesterId;
    Long receiverId;
    RequestStatus status;
    String message;
    Set<Long> skillIds;
}
