package school.faang.user_service.dto.mentorship;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {
    long id;
    String description;
    long requesterId;
    long receiverId;
    RequestStatus status;
    String rejectionReason;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
