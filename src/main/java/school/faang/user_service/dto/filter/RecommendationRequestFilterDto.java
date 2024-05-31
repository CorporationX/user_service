package school.faang.user_service.dto.filter;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Data
public class RecommendationRequestFilterDto {
    private Long id;
    @Positive
    private Long requesterId;
    @Positive
    private Long receiverId;
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    @Positive
    private Long recommendationId;
    private List<SkillRequest> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
