package school.faang.user_service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SkillRepository;

@Component
@AllArgsConstructor
public class SkillValidator {
    private SkillRepository skillRepository;

    public boolean existByTitle(String title) {
        return skillRepository.existsByTitle(title);
    }

    public boolean titleIsValid(String title) {
        return !title.isBlank();
    }

    public boolean isNullableId(Long id) {
        return id == null;
    }
}
