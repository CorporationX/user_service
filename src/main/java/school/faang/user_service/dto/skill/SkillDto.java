package school.faang.user_service.dto.skill;

import lombok.Builder;

@Builder
public record SkillDto(Long id, String title) {
}