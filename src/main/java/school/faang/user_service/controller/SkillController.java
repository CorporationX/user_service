package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        SkillDto createdSkill = null;
        try {
            createdSkill = skillService.create(skill);
        } catch (DataValidationException e) {
            log.warn(e.toString());
        }
        return createdSkill;
    }
}
