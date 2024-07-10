package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {
    long id;
    String description;
    @Positive
    long requesterId;
    @Positive
    long receiverId;
    RequestStatus status;
    String rejectionReason;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
