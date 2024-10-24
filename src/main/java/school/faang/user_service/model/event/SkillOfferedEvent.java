package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillOfferedEvent {
    private Long senderId;
    private Long receiverId;
    private Long skillId;
}
