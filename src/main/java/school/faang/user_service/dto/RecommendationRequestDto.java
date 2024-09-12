package school.faang.user_service.dto;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

public class RecommendationRequestDto {
    private long id;
    private String message;
    private RequestStatus status;
    private List<SkillRequest> skills;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
