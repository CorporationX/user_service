package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FollowerEvent {
    private Long followerId;
    private Long followeeId;
    private LocalDateTime eventTime;
}
