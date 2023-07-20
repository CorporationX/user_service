package school.faang.user_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private long id;
    private String message;
    private RequestStatus status;
    private List<Long> skillsIds;

    private long requestId;
    private long receiverId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
