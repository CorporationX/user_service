package school.faang.user_service.validate.skill;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SkillValidation {
    private final UserRepository userRepository;
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