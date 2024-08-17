package school.faang.user_service.event.mentorship.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestedEvent {

    private final UUID eventId = UUID.randomUUID();
    private long requesterId;
    private long receiverId;
    private LocalDateTime timestamp;
}
