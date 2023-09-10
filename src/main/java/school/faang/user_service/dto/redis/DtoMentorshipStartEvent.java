package school.faang.user_service.dto.redis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoMentorshipStartEvent {
    private Long requesterId;
    private Long receiverId;
}
