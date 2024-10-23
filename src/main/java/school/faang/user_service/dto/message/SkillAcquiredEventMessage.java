package school.faang.user_service.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SkillAcquiredEventMessage {

    private Long receiverId;
    private Long skillId;
    private String skillTitle;
}
