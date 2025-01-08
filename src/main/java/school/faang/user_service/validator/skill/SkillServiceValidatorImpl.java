package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exception.data.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillServiceValidatorImpl implements SkillServiceValidator {

    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public void validateSkillsExist(List<Long> skillIds) {
        if(skillRepository.countExisting(skillIds) != skillIds.size()) {
            throw new DataValidationException("Not all skills exist");
        }
    }
}
