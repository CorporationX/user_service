package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.service.SkillService;

@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        return skillService.create(skill);
    }
}