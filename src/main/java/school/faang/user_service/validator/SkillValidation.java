package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillValidation {
    private final SkillRepository skillRepository;


    public void validateSkillId(long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException("Такого навыка не существует");
        }
    }

    public void validateSkillTitle(SkillDto skill) {
        if (StringUtils.isBlank(skill.getTitle())) {
            throw new DataValidationException("Введите наименование навыка");
        }
    }
}