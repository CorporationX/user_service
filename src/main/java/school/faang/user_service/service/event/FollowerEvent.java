package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FollowerEvent {
    private Long followerId;
    private Long followedUserId;
    private Long followedProjectId;
    private LocalDateTime subscriptionTime;
}