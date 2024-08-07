package school.faang.user_service.event.mentorship.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import school.faang.user_service.event.RedisEvent;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class MentorshipAcceptedEvent extends RedisEvent {

    private long mentorshipRequestId;
    private long requesterId;
    private long receiverId;

}
