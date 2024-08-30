package school.faang.user_service.dto.publishable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestEvent {
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime timestamp;
}
