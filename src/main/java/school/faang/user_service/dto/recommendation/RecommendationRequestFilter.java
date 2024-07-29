package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class RecommendationRequestFilter {
    Long requesterId;
    Long receiverId;
    RequestStatus status;
    String message;
    Set<Long> skillIds;
}
