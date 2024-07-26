package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateSkillTitleIsNotNullAndNotBlank(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            log.error("skill title is null or empty");
            throw new DataValidationException("title is null or empty");
        }
    }

    public void validateSkillTitleDosNotExists(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            log.error("skill title already exists");
            throw new DataValidationException("the skill already exists");
        }
    }

    public void validateSkillExistenceById(Long id) {
        if (skillRepository.findById(id).isEmpty()) {
            log.error("skill does not exist");
            throw new DataValidationException("the skill does not exist");
        }
    }
}