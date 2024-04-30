package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    SkillDto validateSkill(SkillDto skill) throws DataValidationException {
        if (!skill.title().isEmpty()) {
            return skill;
        } else {
            throw new DataValidationException("sfsdf");
        }
    }
}
