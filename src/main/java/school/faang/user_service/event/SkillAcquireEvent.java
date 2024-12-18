package school.faang.user_service.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillAcquireEvent {
    private Long authorId;
    private Long receiverId;
    private Long skillId;
}
