package school.faang.user_service.redis;

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
    private LocalDateTime requestTime;

}
