package school.faang.user_service.dto.skill;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SkillCandidateDto {
    private final SkillDto skill;
    private final int offersAmount;
}
