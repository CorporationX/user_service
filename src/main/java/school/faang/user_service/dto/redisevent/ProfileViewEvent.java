package school.faang.user_service.dto.redisevent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ProfileViewEvent extends AbstractRedisEvent {
    public ProfileViewEvent(long receiverId, long actorId, LocalDateTime receivedAt) {
        super(receiverId, actorId, receivedAt);
    }
}
