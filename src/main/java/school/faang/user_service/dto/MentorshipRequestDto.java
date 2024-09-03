package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {
    private int id;
    String description;
    Long userRequesterId;
    Long userReceiverId;
    RequestStatus status;
    String rejectionReason;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
