package school.faang.user_service.service.redis.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.EventType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipStartEvent {
    private Long mentorId;
    private Long menteeId;
    private EventType eventType;
    private LocalDateTime receivedAt;
}
