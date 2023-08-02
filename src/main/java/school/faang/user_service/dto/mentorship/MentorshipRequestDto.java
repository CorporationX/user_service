package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import java.time.LocalDateTime;

@Data
@Builder
public class MentorshipRequestDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private String rejectionReason;
    private RequestStatus requestStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
