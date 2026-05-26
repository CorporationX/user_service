package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.repository.user.SkillRepository;

@RequiredArgsConstructor
@Component
public class SkillValidator {
    public final SkillRepository skillRepository;

    public void validateCreate(CreateSkillDto skillDto) {
        validateTitleNotNullOrBlank(skillDto);
        validateTitleUnique(skillDto);
    }

    private void validateTitleNotNullOrBlank(CreateSkillDto skillDto) {
        if (skillDto.title() == null || skillDto.title().isBlank()) {
            throw new IllegalArgumentException(
                    "Skill title cannot be null or blank"
            );
        }
    }

    private void validateTitleUnique(CreateSkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new IllegalArgumentException(
                    String.format("Skill with title %s already exists", skillDto.title())
            );
        }
    }
}


