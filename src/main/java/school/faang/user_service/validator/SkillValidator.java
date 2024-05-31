package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;
    private final UserValidator userValidator;

    public void checkSkillIdAndUserIdInDB(Long skillId, Long userId) {
        if (!skillRepository.existsById(skillId)) {
            throw new DataValidationException("Не найден скилл с id: " + skillId);
        }
        userValidator.validateUserExists(userId);
    }


    public void validateTitleRepetition(String title) {
        if (skillRepository.existsByTitle(title)) {
            throw new DataValidationException("Навык с таким именем уже существует в базе данных");
        }
    }

    public void validateSkill(String title) {
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Передан пустой навык");
        }
    }
}