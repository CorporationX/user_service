package school.faang.user_service.model.event;

import lombok.Builder;

@Builder
public record SkillAcquiredEvent(
        long userId,
        long skillId
) {
}
