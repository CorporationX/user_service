package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    private String message;
    private RequestStatus status;
    private List<Long> skills;
    private Long requestId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
