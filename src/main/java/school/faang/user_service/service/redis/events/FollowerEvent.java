package school.faang.user_service.service.redis.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.EventType;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerEvent implements Serializable {
    private EventType eventType;
    private Date receivedAt;
    private Long followerId;
    private Long followeeId;
}
