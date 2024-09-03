package school.faang.user_service.validator.candidate.Skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillCandidateValidator {
    private final SkillRepository skillRepository;

    public void validateCandidateSkill(SkillDto skillDto) throws IllegalAccessException {
        if (skillDto.title().isEmpty()) {
            throw new IllegalAccessException("Validation failed. Skill name is blank");
        }
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new IllegalAccessException("This skill already exists.");
        }
    }
}