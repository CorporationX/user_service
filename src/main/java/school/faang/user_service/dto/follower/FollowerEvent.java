package school.faang.user_service.dto.follower;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.EventType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowerEvent {
    private EventType eventType;
    private LocalDateTime receivedAt;
    private Long followerId;
    private Long followeeId;
}
