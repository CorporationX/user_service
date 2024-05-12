package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillAcquiredEvent {
    private Long skillId;
    private Long authorId;
    private Long receiverId;
}
