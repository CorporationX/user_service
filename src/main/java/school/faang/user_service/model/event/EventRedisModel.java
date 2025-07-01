package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("event")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRedisModel implements Serializable {
    @Id
    private String key;
    @TimeToLive
    private Long ttl;
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private int maxAttendees;
    private EventType type;
    private EventStatus status;
}
