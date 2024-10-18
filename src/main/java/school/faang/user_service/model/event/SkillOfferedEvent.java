package school.faang.user_service.model.event;

import lombok.Builder;

@Builder
public record SkillOfferedEvent(
        long receiverId,
        long senderId,
        long skillId
) {
}
