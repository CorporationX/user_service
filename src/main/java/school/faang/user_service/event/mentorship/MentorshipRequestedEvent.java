package school.faang.user_service.event.mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestedEvent {
    private long requesterId;
    private long receiverId;
    private LocalDateTime timestamp;
}
