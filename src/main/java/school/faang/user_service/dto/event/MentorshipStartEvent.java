package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MentorshipStartEvent {
    private Long mentorId;
    private Long menteeId;
}
