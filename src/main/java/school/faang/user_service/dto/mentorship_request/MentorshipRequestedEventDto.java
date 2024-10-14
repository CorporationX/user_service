package school.faang.user_service.dto.mentorship_request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestedEventDto {
    private long requestId;
    private long receiverId;
    private long actorId;
    private LocalDateTime receivedAt;
}
