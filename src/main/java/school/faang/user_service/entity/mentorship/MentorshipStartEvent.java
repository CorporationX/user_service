package school.faang.user_service.entity.mentorship;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MentorshipStartEvent {
    private long mentorId;
    private long menteeId;
}
