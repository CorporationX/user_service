package school.faang.user_service.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MentorshipStartEvent {
    private Long mentorId;
    private Long menteeId;
}
