package school.faang.user_service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import school.faang.user_service.dto.event.MessageEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class SkillAcquiredEvent extends MessageEvent {

    private Long skillId;

    public SkillAcquiredEvent(long actorId, long receiverId, Long skillId) {
        super(actorId, receiverId);
        this.skillId = skillId;
    }
}
