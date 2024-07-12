package school.faang.user_service.controller.recommendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class RequestFilterDto {
    private String requesterName;
    private String receiverName;
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private Long recommendationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
