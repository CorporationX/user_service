package school.faang.user_service.model.dto.skill;

import lombok.Builder;

@Builder
public record SkillCandidateDto(
        SkillDto skill,
        long offersAmount) {
}