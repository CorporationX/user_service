package school.faang.user_service.dto.redis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorshipStartEventDto {
    private Long requesterId;
    private Long receiverId;
}
