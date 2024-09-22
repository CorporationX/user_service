package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateAllSkillsIdsExist(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(ids);
            if (skills.size() != ids.size()) {
                throw new DataValidationException("There are no skills with those ids");
            }
        }
    }
}
