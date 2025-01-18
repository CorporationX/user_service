package school.faang.user_service.dto.mentorship;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class MentorshipRequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
