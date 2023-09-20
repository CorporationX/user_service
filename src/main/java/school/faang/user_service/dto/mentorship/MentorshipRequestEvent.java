package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MentorshipRequestEvent {
    private long followerId;
    private long followeeId;
    private LocalDateTime localDateTime;
}
