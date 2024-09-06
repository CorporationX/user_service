package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.skill.SkillNotValidException;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class SkillRequestValidator {
    private final SkillRequestRepository skillRequestRepository;

    public void validateSkillsExist(List<SkillDto> skillsDto) {
        for (SkillDto skill : skillsDto) {
            if (skillRequestRepository.findById(skill.getId()).isEmpty()) {
                log.error("Skill not in DB!");
                throw new SkillNotValidException("Skills don't exist in DB!");
            }
        }
    }
}
