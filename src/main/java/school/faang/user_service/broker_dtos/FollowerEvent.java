package school.faang.user_service.broker_dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class FollowerEvent {
    private int followerId;
    private int followeeId;
    private int projectId;
    private LocalDateTime subscriptionTime;
}
