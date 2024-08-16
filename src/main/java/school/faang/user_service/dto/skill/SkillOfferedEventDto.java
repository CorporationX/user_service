package school.faang.user_service.dto.skill;

import lombok.Builder;

@Builder
public record SkillOfferedEventDto(
    long receiverId,
    long senderId,
    long skillId
) {
}
