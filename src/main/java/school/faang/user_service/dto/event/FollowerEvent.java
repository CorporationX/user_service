package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowerEvent {
    private long id;
    private long follower;
    private long followee;
    private LocalDateTime followedAt;
}
