package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.List;

@Data
@AllArgsConstructor
public class SkillAcquiredEvent {
    private long skillId;
    private long receiverId;
    private List<UserSkillGuarantee> guarantees;
}
