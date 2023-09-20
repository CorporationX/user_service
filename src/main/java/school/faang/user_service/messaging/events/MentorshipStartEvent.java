package school.faang.user_service.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MentorshipStartEvent {
    private Long mentorId;
    private Long menteeId;
}
