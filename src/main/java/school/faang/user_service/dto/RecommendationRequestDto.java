package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private Long recommendationId;
    private List<Long> skillsId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
