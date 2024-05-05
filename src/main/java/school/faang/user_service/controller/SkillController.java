package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Slf4j
@Component
@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }
}