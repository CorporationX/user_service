package school.faang.user_service.dto.skill;

import lombok.Builder;

@Builder
public record SkillCandidateDto(SkillDto skill, long offersAmount) {
}