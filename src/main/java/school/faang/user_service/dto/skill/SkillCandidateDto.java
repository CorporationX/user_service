package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.PositiveOrZero;

public record SkillCandidateDto(
        SkillDto skill,
        @PositiveOrZero(message = "Amount of offers can't be negative")
        long offersAmount
) {
}