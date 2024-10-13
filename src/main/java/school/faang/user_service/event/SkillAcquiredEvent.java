package school.faang.user_service.event;

import lombok.Builder;

@Builder
public record SkillAcquiredEvent(
        long userId,
        long skillId
) {
}
