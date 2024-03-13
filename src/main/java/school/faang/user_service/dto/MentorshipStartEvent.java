package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MentorshipStartEvent {
    private long mentorId;
    private long menteeId;
    private LocalDateTime timestamp;
}
