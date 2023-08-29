package school.faang.user_service.dto.skill;

import lombok.Data;

@Data
public class SkillOfferEvent {
    private Long senderId;
    private Long receiverId;
    private Long skillId;
}
