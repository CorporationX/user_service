package school.faang.user_service.dto.recommendation;

<<<<<<< HEAD
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.time.LocalDateTime;

@Data
public class RecommendationRequestDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
=======
import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationRequestDto {
    private long id;
>>>>>>> medusa-feature-BC-3526
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private Recommendation recommendation;
    private List<Skill> skills;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
