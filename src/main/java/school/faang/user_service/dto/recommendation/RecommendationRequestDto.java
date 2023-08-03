package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.time.LocalDateTime;

@Data
public class RecommendationRequestDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private Recommendation recommendation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
