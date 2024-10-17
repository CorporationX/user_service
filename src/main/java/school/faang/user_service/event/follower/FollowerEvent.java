package school.faang.user_service.event.follower;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerEvent {

    private Long followerId;
    private Long followeeId;
    private LocalDateTime created;
}