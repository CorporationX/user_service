package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MentorshipAcceptedEvent extends MessageEvent {

    private long requestId;

    public MentorshipAcceptedEvent(long actorId, long receiverId, long id) {
        super(actorId, receiverId);
        requestId = id;
    }
}
