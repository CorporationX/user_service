package school.faang.user_service.dto.recomendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private RequestStatus status;
    private List<SkillRequest> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
