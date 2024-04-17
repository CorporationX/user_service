package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder

public class FollowerEvent {

    private long followerId;
    private long followeeId;
    private long projectId;
    private LocalDateTime subscriptionDateTime;

}