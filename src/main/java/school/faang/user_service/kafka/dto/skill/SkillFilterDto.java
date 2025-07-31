package school.faang.user_service.kafka.dto.skill;

import lombok.Builder;

@Builder
public record SkillFilterDto(
    long id,
    String name
) {
}
