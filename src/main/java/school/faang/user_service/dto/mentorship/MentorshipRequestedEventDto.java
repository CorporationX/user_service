package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MentorshipRequestedEventDto {
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime receivedAt;
}
