package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    private long id;
    private long requesterId;
    private long receiverId;
    private RequestStatus status;
    private String rejectionReason;
    private long recommendationId;
    private LocalDateTime createdAt;
}
