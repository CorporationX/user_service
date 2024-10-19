package school.faang.user_service.dto.redisevent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractRedisEvent {
    private long receiverId;
    private long actorId;
    private LocalDateTime receivedAt;
}
