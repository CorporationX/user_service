package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecommendationRequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private String messageContains;
    private RequestStatus status;
}
