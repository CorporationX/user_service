package school.faang.user_service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MentorshipRequestedEvent extends MessageEvent {

    private LocalDateTime receivedAt;

    public MentorshipRequestedEvent(long actorId, long receiverId, LocalDateTime receivedAt) {
        super(actorId, receiverId);
        this.receivedAt = receivedAt;
    }
}
