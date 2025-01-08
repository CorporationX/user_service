package school.faang.user_service.validator.skill;

import java.util.List;

public interface SkillServiceValidator {

    void validateSkillsExist(List<Long> skillIds);
}
