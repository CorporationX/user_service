package school.faang.user_service.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class MentorshipEvent extends Event {
    private long requesterId;
    private long userId;
}
