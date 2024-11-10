package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record SkillCandidateDto(
        SkillDto skillDto,
        Long offersAmount
) {
}
