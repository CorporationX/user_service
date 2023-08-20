package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MentorshipRequestedEvent {
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
}
