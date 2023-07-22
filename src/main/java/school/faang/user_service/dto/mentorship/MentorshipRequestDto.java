package school.faang.user_service.dto.mentorship;


import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class MentorshipRequestDto {

    private long id;
    private String description;
    private UserDto requester;
    private UserDto receiver;
    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
