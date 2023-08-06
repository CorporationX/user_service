package school.faang.user_service.dto.recommendation.filter;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestFilterDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private Long recommendationId;
    private List<Long> skillIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
